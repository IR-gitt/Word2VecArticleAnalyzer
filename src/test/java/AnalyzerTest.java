import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static analyzerWord2Vec.ArticleAnalyzer.*;
import static analyzerWord2Vec.OperationForAnalyzedData.createTokensForAnalysis;

public class AnalyzerTest {
    static String filePathAstr = Paths.get("src/main/java/analyzerWord2Vec/forFit/astronomy.txt")
            .toString();

    // Сравнение и вывод элемента с наиболее большим коэф совпадения
    @Test
    // сравнение векторов двух предложений
    public void cosineSimilarityTwoSentence() throws IOException {

        String sentence1 = "относительно компонентов таблица имеет ошибки";
        String sentence2 = "относительно имеет ошибки";
        // получение листа слов для обучения
        List<String> wordsList = createTokensForAnalysis(filePathAstr);

        // созадние модели
        Word2Vec word2Vec = createModel(createDataForLearnModel(wordsList));

        // вычисление косинусного сходства
        double comparisonsRes = cosineSimilarityTwoSentence(word2Vec, sentence1, sentence2);
    }

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

}
