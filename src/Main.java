import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class Main {

        public static void main(String[] args) throws IOException
        {
                Scanner input = new Scanner(System.in);

                System.out.print("Give your train//pos path : ");
                String path1 = input.nextLine();
                System.out.print("Give your train//neg path : ");
                String path2 = input.nextLine();
                System.out.print("Give your test//pos path : ");
                String path3 = input.nextLine();
                System.out.print("Give your test//neg path : ");
                String path4 = input.nextLine();


                input.close();

                Dev_Data develop = new Dev_Data(new File(path1),new File(path2),new File(path3),new File(path4),100);

                develop.dev();

                System.out.println("Trained "+ develop.get_len_of_train_pos_files() + " positive reviews and "
                        + develop.get_len_of_train_neg_files() + " negative reviews .");

                System.out.println("Testing "+ Objects.requireNonNull(develop.get_folder_of_test_pos_files().listFiles()).length+ " positive reviews and "
                        + Objects.requireNonNull(develop.get_folder_of_test_neg_files().listFiles()).length + " negative reviews .");

                System.out.println(develop.vocab_size_toString());

                develop.get_final_MNBayes().Test(develop.get_folder_of_test_pos_files(),develop.get_folder_of_test_neg_files(),0,0);

        }
}
