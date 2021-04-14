import java.io.File;
import java.io.IOException;
import java.util.*;

public class Multinomial_Naive_Bayes {

    private final Vocab vocabulary;

    private final HashMap<String,Double> POS_list;
    private final HashMap<String,Double> NEG_list;

    private int positive_files_length = 0;
    private int negative_files_length = 0;
    private int total_files_length = 0;
    private double POS_prior_probability , NEG_prior_probability;
    private double accuracy;


    public Multinomial_Naive_Bayes(Vocab v) {
        this.vocabulary = v;
        this.POS_list = new HashMap<>();
        this.NEG_list = new HashMap<>();

    }

    public Multinomial_Naive_Bayes(Multinomial_Naive_Bayes newBayes){
        this.vocabulary = newBayes.vocabulary;
        this.POS_list = newBayes.POS_list;
        this.NEG_list = newBayes.NEG_list;
        this.positive_files_length = newBayes.positive_files_length;
        this.negative_files_length = newBayes.negative_files_length;
        this.total_files_length = newBayes.total_files_length;
        this.NEG_prior_probability =newBayes.NEG_prior_probability;
        this.POS_prior_probability =newBayes.POS_prior_probability;
    }

    private void read_folder(Review_Analyzer r , File folder , int size) throws IOException
    {
        int i = 0 ;
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (i==size){
                break;
            }
            if (file.isFile()) {
                r.split(folder.getPath(), file.getName());
                r.probability_Counter();
                r.clear_review_list();
            } else {
                break;
            }
            i++;
        }
    }

    public ArrayList<String> get_vocabulary(){
        return this.vocabulary.get_vocab();
    }

    public void Train(File positive_folder_ ,File negative_folder_,int size_pos_end , int size_neg_end) throws IOException {

        Review_Analyzer POS = new Review_Analyzer();
        POS.create_vocab_hash(this.vocabulary);
        read_folder(POS , positive_folder_,size_pos_end);

        Review_Analyzer NEG = new Review_Analyzer();
        NEG.create_vocab_hash(this.vocabulary);
        read_folder(NEG , negative_folder_,size_neg_end);

        double laplace_smoothing = 1.0;
        POS.calculate_probability(laplace_smoothing);
        NEG.calculate_probability(laplace_smoothing);

        this.POS_list.putAll(POS.get_vocab_with_probabilities());
        this.NEG_list.putAll(NEG.get_vocab_with_probabilities());

        this.positive_files_length = POS.getFiles_length();
        this.negative_files_length = NEG.getFiles_length();
        this.total_files_length = this.positive_files_length + this.negative_files_length;

        this.POS_prior_probability = (double)this.positive_files_length /this.total_files_length;
        this.NEG_prior_probability = (double)this.negative_files_length /this.total_files_length;

    }

    public double Test(File positive_folder_ , File negative_folder_ , int start_size_pos , int start_size_neg)
    {
        int correct = 0;
        int wrong = 0;
        int truePositive = 0;
        int trueNegative = 0;
        int falsePositive = 0;
        int falseNegative = 0;

        try{
            Review_Analyzer TEST_POS = new Review_Analyzer();

            int count_pos = 0 ;
            for (File file : Objects.requireNonNull(positive_folder_.listFiles())) {
                if (count_pos < start_size_pos){
                    count_pos++;
                    continue;
                }

                if (file.isFile()) {
                    TEST_POS.split(positive_folder_.getPath(), file.getName());
                    double pos_probability_score = this.POS_prior_probability;
                    for ( int i = 0 ; i <TEST_POS.get_review().size(); i++) {
                        for (String s : this.POS_list.keySet()) {
                            if (TEST_POS.get_review().get(i).equals(s)) {
                                pos_probability_score *= this.POS_list.get(s);
                                break;
                            }
                        }
                    }
                    double neg_probability_score = this.NEG_prior_probability;
                    for ( int i = 0 ; i <TEST_POS.get_review().size(); i++) {
                        for (String s : this.NEG_list.keySet()) {
                            if (TEST_POS.get_review().get(i).equals(s)) {
                                neg_probability_score *= this.NEG_list.get(s);
                                break;
                            }
                        }
                    }
                    if (pos_probability_score > neg_probability_score){
                        correct++;
                        truePositive++;
                    }
                    else if (pos_probability_score < neg_probability_score){
                        wrong++;
                        falseNegative++;
                    }
                }
                TEST_POS.clear_review_list();
                count_pos++;
            }

            Review_Analyzer TEST_NEG = new Review_Analyzer();
            int count_neg = 0 ;
            for (File file : Objects.requireNonNull(negative_folder_.listFiles())) {
                if (count_neg < start_size_neg){
                    count_neg++;
                    continue;
                }
                if (file.isFile()) {
                    TEST_NEG.split(negative_folder_.getPath(), file.getName());
                    double pos_probability_score = (double)this.positive_files_length/this.total_files_length;
                    for ( int i = 0 ; i <TEST_NEG.get_review().size(); i++) {
                        for (String s : this.POS_list.keySet()) {
                            if (TEST_NEG.get_review().get(i).equals(s)) {
                                pos_probability_score *= this.POS_list.get(s);
                                break;
                            }
                        }
                    }
                    double neg_probability_score = (double)this.negative_files_length/this.total_files_length;
                    for ( int i = 0 ; i <TEST_NEG.get_review().size(); i++) {
                        for (String s : this.NEG_list.keySet()) {
                            if (TEST_NEG.get_review().get(i).equals(s)) {
                                neg_probability_score *= this.NEG_list.get(s);
                                break;
                            }
                        }
                    }
                    if (pos_probability_score > neg_probability_score){
                        wrong++;
                        falsePositive++;
                    } else if (pos_probability_score < neg_probability_score){
                        correct++;
                        trueNegative++;
                    }
                }
                TEST_NEG.clear_review_list();
                count_neg++;
            }

            this.accuracy = (double)correct/(TEST_POS.getFiles_length()+ TEST_NEG.getFiles_length());
            double precision_positive = (double)truePositive/(falsePositive+truePositive);
            double recall_positive = (double)truePositive/(falseNegative+truePositive);
            double precision_negative = (double)trueNegative/(falseNegative+trueNegative);
            double recall_negative = (double)trueNegative/(falsePositive+trueNegative);

            double precision = (double)1/2*(precision_positive + precision_negative);
            double recall = (double)1/2*(recall_positive + recall_negative);

            System.out.println("Accuracy :" + this.accuracy );
            System.out.println("Correct : " + correct +" files " +"from "+ (TEST_POS.getFiles_length()+ TEST_NEG.getFiles_length()) + " files");
            System.out.println("Error rate : " + (1 - this.accuracy));
            System.out.println("Precision " +   precision );
            System.out.println("Recall : " + recall );
            System.out.println("F1 score : " + 2*(precision*recall)/(precision + recall) );
            System.out.println();

            return this.accuracy;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double get_Accuracy(){
        return this.accuracy;
    }

}
