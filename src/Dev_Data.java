import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Dev_Data {

    private final File folder_of_train_pos_files;
    private final File folder_of_train_neg_files;
    private final File folder_of_test_pos_files;
    private final File folder_of_test_neg_files;
    private final int additioner;
    private Multinomial_Naive_Bayes mainMNB = null;
    private int vocab_start;
    private int vocab_end;
    private int len_of_train_pos , len_of_train_neg;

    public Dev_Data(File f1 ,File f2 , File f3 , File f4 , int add){
        this.folder_of_train_pos_files = f1;
        this.folder_of_train_neg_files = f2;
        this.folder_of_test_pos_files = f3;
        this.folder_of_test_neg_files = f4;
        this.additioner = add;
    }

    public void dev() throws IOException
    {
        this.len_of_train_pos = Objects.requireNonNull(this.folder_of_train_pos_files.listFiles()).length *90/100; // counting starts from 0 until x% of folder's size
        this.len_of_train_neg = Objects.requireNonNull(this.folder_of_train_neg_files.listFiles()).length *90/100;

        double max_Acc = 0.0;
        int vocab_Start_position = 100;
        int vocab_End_position = 600;

        //dev mode
        System.out.println("Dev mode ... ");
        while (vocab_End_position <= 1000) {
            // dev files from train folder (5%)
            int TEMP_test_pos_len_files = Objects.requireNonNull(this.folder_of_train_pos_files.listFiles()).length*90/100; // counting starts from 90% of folder's size
            int TEMP_test_neg_len_files = Objects.requireNonNull(this.folder_of_train_neg_files.listFiles()).length*90/100;

            Review_Analyzer s = new Review_Analyzer();
            s.reamFromFile("imdb.vocab");

            Vocab vocab = new Vocab(s.get_review());
            vocab.construct_vocab(vocab_Start_position, vocab_End_position);

            System.out.println("Test vocabulary {" + vocab_Start_position + "..."+ vocab_End_position +"}\n ------------" );

            Multinomial_Naive_Bayes temp = new Multinomial_Naive_Bayes(vocab);

            temp.Train(this.folder_of_train_pos_files, this.folder_of_train_neg_files, this.len_of_train_pos, this.len_of_train_neg);

            double temp_accuracy =  temp.Test(this.folder_of_train_pos_files, this.folder_of_train_neg_files, TEMP_test_pos_len_files, TEMP_test_neg_len_files);
            if (temp_accuracy >= max_Acc)
            {
                max_Acc = temp_accuracy;
                this.mainMNB = new Multinomial_Naive_Bayes(temp);
                this.vocab_start = vocab_Start_position;
                this.vocab_end = vocab_End_position;
            }
            vocab_Start_position += this.additioner;
            vocab_End_position += this.additioner;
        }
    }

    public Multinomial_Naive_Bayes get_final_MNBayes(){
        assert this.mainMNB != null;
        return  this.mainMNB;
    }

    public File get_folder_of_test_pos_files(){
        return this.folder_of_test_pos_files;
    }

    public File get_folder_of_test_neg_files(){
        return this.folder_of_test_neg_files;
    }

    public int get_len_of_train_pos_files(){
        return this.len_of_train_pos;
    }

    public int get_len_of_train_neg_files(){
        return this.len_of_train_neg;
    }

    public int getVocab_start() { return  this.vocab_start;}

    public int getVocab_end() { return  this.vocab_end;}

    public String vocab_size_toString(){ return "Selected vocabulary {" + this.vocab_start + "..."+ this.vocab_end +"}\n ------------";}
}
