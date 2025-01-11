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
import java.util.List;

import static analyzerWord2Vec.OperationForAnalyzedData.createTokensForAnalysis;

public class ArticleAnalyzer {

    static String filePathModel =
            "src/main/java/analyzerWord2Vec/fileModel.txt";

    public Map<String, Double> startAnalyzer(String textForAnalyzer) throws Exception {
        String filePathLaw = Paths.get("src/main/java/analyzerWord2Vec/forFit/law.txt").toString();
        String filePathEco = Paths.get("src/main/java/analyzerWord2Vec/forFit/economics.txt").toString();
        String filePathAst = Paths.get("src/main/java/analyzerWord2Vec/forFit/astronomy.txt").toString();
        ArrayList<String> pathsToSets = new ArrayList<>();
        pathsToSets.add(filePathLaw);
        pathsToSets.add(filePathEco);
        pathsToSets.add(filePathAst);
        System.out.println(textForAnalyzer+"1111");
        return compareValue(pathsToSets, textForAnalyzer);
    }


    private static Map<String, Double> compareValue(ArrayList<String> filePathModel, String sentence)
            throws IOException {

        Map<String, Double> resultsCompare = new HashMap<>();

        double resultCompare;

        for (String filePath : filePathModel) {
            // разделение предложения на точки и экранирование
            String[] modelName = filePath.split("\\\\|\\.");

            // создание набора слов для модели
            Collection<String> wordsList = createDataForLearnModel(createTokensForAnalysis(filePath));

            //Collection<String> words = wordsList);
            // создание модели
            Word2Vec word2Vec = createModel(wordsList);

            // получение слов из созданной модели
            Collection<String> wordsInModel = word2Vec.vocab().words();

            // сравнение векторов модели и предложения
            resultCompare = comparisonsCosineSimilarity(word2Vec, sentence, wordsInModel);
            resultsCompare.put(modelName[5], resultCompare);
        }
        return resultsCompare;
    }

    //получаем значения c текстом для обучения и обучаем модель
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

        // Вычислим косинусное сходство между векторами
        return cosineSimilarity(Objects.requireNonNull(vec1), vec2);
    }

    // Вычисление среднего вектора предложения
    public static INDArray getAverageVector(String[] words, WordVectors wordVectors) {
        INDArray totalVector;
        System.out.println(words[0]);
        System.out.println(wordVectors);
        if (wordVectors.getWordVector(words[0]) == null) {

            totalVector = Nd4j.zeros(1);
        } else {
            totalVector =
                    // созадние нулевого вектора для первого слова
                    Nd4j.zeros(
                            wordVectors.getWordVectorMatrix(words[0])
                                    .length());
        }

        int numWords = 0;

        for (String word : words) {
            if (wordVectors.hasWord(word)) {
                // добавлнеие вектора к общему значению
                totalVector.addi(
                        wordVectors.getWordVectorMatrix(word));
                numWords++;
            }
        }

        if (numWords > 0) {
            // разделим на количество слов
            return totalVector.divi(numWords);
        } else {
            return null;
        }
    }

    // Вычисление косинусного сходства между векторами
    //todo: при значении null падает
    public static double cosineSimilarity(INDArray vec1, INDArray vec2) {
        double dotProduct = vec1.mul(vec2).sumNumber().doubleValue();
        double norm1 = vec1.norm2Number().doubleValue();
        double norm2 = vec2.norm2Number().doubleValue();

        if (norm1 > 0 && norm2 > 0) {
            return dotProduct / (norm1 * norm2);
        } else {
            return 0.0;
        }
    }

    // получение текста для обучения модели
    public static Collection<String> createDataForLearnModel(List<String> dataFrame) {
        Collection<String> textForLearn = new ArrayList<>();
        for (Object word : dataFrame) {
            if (word != null) {
                textForLearn.add(word.toString().toLowerCase().replaceAll(",", ""));
            }
        }
        return textForLearn;
    }
}







