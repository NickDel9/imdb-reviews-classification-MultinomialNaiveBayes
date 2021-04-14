import java.util.ArrayList;

public class Vocab {

    private final ArrayList<String> words;
    private final ArrayList<String> vocabulary;

    public Vocab(ArrayList<String> words ){
        this.words = words;
        vocabulary = new ArrayList<>();
    }

    public void construct_vocab( int n , int k){
        for (int i = n ; i<=k;i++){
                this.vocabulary.add(this.words.get(i));
        }
    }

    public ArrayList<String> get_vocab(){
        return  this.vocabulary;
    }

}
