
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Review_Analyzer {

    private final ArrayList<String> review;

    private final HashMap<String , Integer> vocab;
    private final HashMap<String , Double> vocab_with_probabilities ;
    private int files_length;

    public Review_Analyzer()
    {
        this.review = new ArrayList<>();
        this.vocab_with_probabilities= new HashMap<>();
        this.vocab = new HashMap<>();
    }

    public void reamFromFile(String inputstream)
    {
        try{
            File file = new File(inputstream);
            Scanner br = new Scanner(file);
            while (br.hasNextLine())
            {
                String data = br.nextLine();
                this.review.add(data);
            }
            br.close();
        }catch (FileNotFoundException e)
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void split(String path , String input) throws IOException
    {
        this.files_length++;
        BufferedReader bufReader = new BufferedReader(new FileReader(path +"\\"+input));
        String line = bufReader.readLine();
        String temp = "";
        for (int i = 0; i<line.length();i++) {
            if (line.charAt(i)==' ' || line.charAt(i) == '.' || line.charAt(i) == ',' || line.charAt(i) == '?' || line.charAt(i) == ')' || line.charAt(i) == '('
            || line.charAt(i) == ':' || line.charAt(i) == ';') {
                if (temp.length()==0)
                {
                    continue;
                }
                this.review.add(temp);
                temp="";
            }
            else{
                temp+=line.charAt(i);
            }
        }
    }

    public void create_vocab_hash(Vocab b){
        for(String i : b.get_vocab()){
            this.vocab.put(i,0);
        }
    }

    public void probability_Counter()
    {
        for (String s : this.review){
            if (this.vocab.containsKey(s)) {
                this.vocab.put(s, this.vocab.get(s) + 1);
            }
        }
    }

    public void calculate_probability(double laplace_smoothing)
    {
        int sum_of_words = 0 ;
        for (String s : this.vocab.keySet()){
            sum_of_words += this.vocab.get(s);
        }

        for (String s : this.vocab.keySet())
        {
            this.vocab_with_probabilities.put(s ,((double)this.vocab.get(s) + laplace_smoothing) / (sum_of_words + (laplace_smoothing*this.files_length)));
        }
    }

    public void clear_review_list()
    {
        this.review.clear();
    }

    public ArrayList<String> get_review()
    {
        return this.review;
    }

    public HashMap<String,Double> get_vocab_with_probabilities(){
        return this.vocab_with_probabilities;
    }

    public int getFiles_length(){
        return this.files_length;
    }

    public HashMap<String , Integer> getVocab(){
        return this.vocab;
    }
}
