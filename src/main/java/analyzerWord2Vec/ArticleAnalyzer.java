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

import static analyzerWord2Vec.OperationForAnalyzedData.createDataForAnalysis;

public class ArticleAnalyzer {
    //final int a =
    static String filePathModel =
            "src/main/java/analyzerWord2Vec/fileModel.txt";

    static String filePathAstr = Paths.get("src/main/java/analyzerWord2Vec/forFit/astronomy.txt")
            .toString();


    public String startAnalyzer(String textForAnalyzer) throws Exception {
        // todo: может быть не одно решение, сделать аналитический круг или диаграмму

        String filePathLaw = Paths.get("src/main/java/analyzerWord2Vec/forFit/law.txt").toString();
        String filePathEco = Paths.get("src/main/java/analyzerWord2Vec/forFit/echonomic.txt").toString();
        String filePathAstr = Paths.get("src/main/java/analyzerWord2Vec/forFit/astronomy.txt").toString();
        ArrayList <String> pathsToSets = new ArrayList<>();
        pathsToSets.add(filePathLaw);
        pathsToSets.add(filePathEco);
        pathsToSets.add(filePathAstr);

        System.out.println();

        String sentence1 = "относительно компонентов таблица имеет ошибки";
        String sentence2 = "относительно имеет ошибки";
        cosineSimilarityTwoSentence(sentence1, sentence2);
        return compareValue(pathsToSets, textForAnalyzer).toString();
    }

    private static Map<String, Double> compareValue(ArrayList <String> filePathModel, String sentence) throws IOException {
        Map <String, Double> resultsCompare = new HashMap<>();

        double resultCompare;

        for (String filePath: filePathModel) {
            // разделение предложения на точки и экранирование
            String[] modelName = filePath.split("\\\\|\\.");

            //todo: установить название модели и значение
            // создание набора слов для модели
            List<String> wordsList = createDataForAnalysis(filePath);

            // создание модели
            Word2Vec word2Vec = createModel(createDataForLearnModel(wordsList));

            // получение слов из созданной модели
            Collection<String> wordsInModel = word2Vec.vocab().words();

            // сравнение векторов модели и предложения
            resultCompare = comparisonsCosineSimilarity(word2Vec, sentence, wordsInModel);
            resultsCompare.put(modelName[5], resultCompare);
        }
        return resultsCompare;
    }

    //получаем значения c текстом для обучения и обучаем модель
    private static Word2Vec createModel(Collection<String> textForLearn)  {

        SentenceIterator iter = new CollectionSentenceIterator(textForLearn);
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());

        // Обучение модели
        Word2Vec vec = new Word2Vec.Builder()
                .minWordFrequency(5)
                .iterations(5)
                .layerSize(100)
                .seed(42)
                .windowSize(5)
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

    // Сравнение и вывод элемента с наиболее большим коэф совпадения
    private static double cosineSimilarityTwoSentence(Word2Vec word2Vec, String sentence1, String sentence2) {

        // Загружаем модель
        WordVectors vecLoad = word2Vec;

        // Разделим предложения на слова
        String[] words1 = sentence1.split(" ");
        String[] words2 = sentence2.split(" ");

        // Вычислим средний вектор для каждого предложения
        INDArray vec1 = getAverageVector(words1, vecLoad);
        INDArray vec2 = getAverageVector(words2, vecLoad);

        // Вычислим косинусное сходство между векторами
        return cosineSimilarity(Objects.requireNonNull(vec1), vec2);
    }

    private static double comparisonsCosineSimilarity(Word2Vec word2Vec, String sentence1,
                                                      Collection<String> sentence2) throws IOException {

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
    public static Collection<String> createDataForLearnModel(List <String> dataFrame) {
        Collection<String> textForLearn = new ArrayList<>();
        //System.out.println(dataFrame.col(21));
        for (Object word : dataFrame) {
            if (word != null) {
                textForLearn.add(word.toString().toLowerCase().replaceAll(",", ""));
                System.out.println(word);
            }
        }
        return textForLearn;
    }

    // сравнение векторов двух предложений
    public static void cosineSimilarityTwoSentence(String sentence1, String sentence2) throws IOException {

        // получение листа слов для обучения
        List<String> wordsList = createDataForAnalysis(filePathAstr);

        // созадние модели
        Word2Vec word2Vec = createModel(createDataForLearnModel(wordsList));

        // вычисление косинусного сходства
        double comparisonsRes = cosineSimilarityTwoSentence(word2Vec, sentence1, sentence2);
        System.out.println(comparisonsRes);
    }
}







