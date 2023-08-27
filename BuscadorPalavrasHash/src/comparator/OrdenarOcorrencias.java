package comparator;

import java.util.Comparator;
import buscadorpalavrashash.Palavra;

public class OrdenarOcorrencias implements Comparator<Palavra>{

    @Override
    public int compare(Palavra p1, Palavra p2) {
        if(p1.getOcorrencia() < p2.getOcorrencia()){
            return 1;
        }
        return -1;
    }
   
}
