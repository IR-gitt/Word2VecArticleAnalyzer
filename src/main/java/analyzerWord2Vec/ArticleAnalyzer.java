package analyzerWord2Vec;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static analyzerWord2Vec.OperationForAnalyzedData.createTokensForAnalysis;

public class ArticleAnalyzer {

    private static final Logger logger = Logger.getLogger(ArticleAnalyzer.class.getName());

    static String filePathModel = "src/main/java/analyzerWord2Vec/fileModel.txt";

    public Map<String, Double> startAnalyzer(String textForAnalyzer) throws Exception {
        String filePathLaw = Paths.get("src/main/java/analyzerWord2Vec/forFit/law.txt").toString();
        String filePathEco = Paths.get("src/main/java/analyzerWord2Vec/forFit/economics.txt").toString();
        String filePathAst = Paths.get("src/main/java/analyzerWord2Vec/forFit/astronomy.txt").toString();

        ArrayList<String> pathsToSets = new ArrayList<>();
        pathsToSets.add(filePathLaw);
        pathsToSets.add(filePathEco);
        pathsToSets.add(filePathAst);

        return compareValue(pathsToSets, textForAnalyzer);
    }

    private static Map<String, Double> compareValue(ArrayList<String> filePathModel, String sentence)
            throws IOException {

        Map<String, Double> resultsCompare = new HashMap<>();

        for (String filePath : filePathModel) {

            String fileName = Paths.get(filePath).getFileName().toString();
            String categoryName = fileName.substring(0, fileName.lastIndexOf('.'));

            // Создание набора слов для модели
            Collection<String> wordsList = createDataForLearnModel(createTokensForAnalysis(filePath));

            // Создание модели
            Word2Vec word2Vec = createModel(wordsList);

            // Получение слов из созданной модели
            Collection<String> wordsInModel = word2Vec.vocab().words();

            // Сравнение векторов модели и предложения
            double resultCompare = comparisonsCosineSimilarity(word2Vec, sentence, wordsInModel);
            resultsCompare.put(categoryName, resultCompare);

            logger.log(Level.INFO, "Category: {0}, Similarity: {1}",
                    new Object[]{categoryName, resultCompare});
        }

        return resultsCompare;
    }

    // Получаем значения с текстом для обучения и обучаем модель
    public static Word2Vec createModel(Collection<String> textForLearn) {

        SentenceIterator iter = new CollectionSentenceIterator(textForLearn);
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());

        // Обучение модели
        Word2Vec vec = new Word2Vec.Builder()
                .minWordFrequency(5)
                .iterations(10)
                .layerSize(100)
                .seed(1)
                .windowSize(3)
                .iterate(iter)
                .tokenizerFactory(t)
                .build();

        vec.fit();

        // Запись векторов в файл
        try {
            WordVectorSerializer.writeWordVectors(vec, filePathModel);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to write word vectors", e);
            throw new RuntimeException(e);
        }

        return vec;
    }

    private static double comparisonsCosineSimilarity(Word2Vec word2Vec, String sentence1,
                                                      Collection<String> sentence2) {
        // Разделим предложения на слова
        String[] words1 = sentence1.split(" ");
        String[] words2 = sentence2.toArray(new String[0]);

        // Вычислим средний вектор для каждого
        INDArray vec1 = getAverageVector(words1, word2Vec);
        INDArray vec2 = getAverageVector(words2, word2Vec);

        // Проверка на null перед вычислением
        if (vec1 == null || vec2 == null) {
            logger.log(Level.WARNING, "Unable to compute similarity - one or both vectors are null");
            return 0.0; // Возвращаем 0 если векторы не могут быть вычислены
        }

        // Вычислим косинусное сходство между векторами
        return cosineSimilarity(vec1, vec2);
    }

    // Вычисление среднего вектора предложения
    public static INDArray getAverageVector(String[] words, WordVectors wordVectors) {
        if (words == null || words.length == 0) {
            logger.log(Level.WARNING, "No words provided for vector calculation");
            return null;
        }

        // Проверка первого слова
        INDArray totalVector;

        // Найдем первое слово, которое есть в модели
        INDArray firstVector = null;
        for (String word : words) {
            if (wordVectors.hasWord(word)) {
                firstVector = wordVectors.getWordVectorMatrix(word);
                break;
            }
        }

        if (firstVector == null) {
            logger.log(Level.WARNING, "No words found in vocabulary");
            return null;
        }

        // Создаем нулевой вектор правильного размера
        totalVector = Nd4j.zeros(firstVector.length());

        int numWords = 0;

        for (String word : words) {
            if (wordVectors.hasWord(word)) {
                // Добавление вектора к общему значению
                totalVector.addi(wordVectors.getWordVectorMatrix(word));
                numWords++;
            }
        }

        if (numWords > 0) {
            // Разделим на количество слов
            return totalVector.divi(numWords);
        } else {
            logger.log(Level.WARNING, "No valid words found for averaging");
            return null;
        }
    }

    // Вычисление косинусного сходства между векторами
    public static double cosineSimilarity(INDArray vec1, INDArray vec2) {
        // проверка на null
        if (vec1 == null || vec2 == null) {
            logger.log(Level.WARNING, "Cannot compute cosine similarity - null vector provided");
            return 0.0;
        }

        double dotProduct = vec1.mul(vec2).sumNumber().doubleValue();
        double norm1 = vec1.norm2Number().doubleValue();
        double norm2 = vec2.norm2Number().doubleValue();

        if (norm1 > 0 && norm2 > 0) {
            return dotProduct / (norm1 * norm2);
        } else {
            logger.log(Level.WARNING, "Zero norm detected in cosine similarity calculation");
            return 0.0;
        }
    }

    // Получение текста для обучения модели
    public static Collection<String> createDataForLearnModel(List<String> dataFrame) {
        Collection<String> textForLearn = new ArrayList<>();

        for (Object word : dataFrame) {
            if (word != null) {
                textForLearn.add(word.toString().toLowerCase().replaceAll(",", ""));
            }
        }

        logger.log(Level.INFO, "Created training data with {0} words", textForLearn.size());
        return textForLearn;
    }
}