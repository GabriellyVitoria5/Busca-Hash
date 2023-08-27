package buscadorpalavrashash;

import java.io.File;

public class Palavra {
    private String palavra;
    private File arquivoTxt;
    private int ocorrencia;

    public Palavra(String palavra, File arquivoTxt, int ocorrencia) {
        this.palavra = palavra;
        this.arquivoTxt = arquivoTxt;
        this.ocorrencia = ocorrencia;
    }
    
    public String getPalavra() {
        return palavra;
    }

    public void setPalavra(String palavra) {
        this.palavra = palavra;
    }

    public File getArquivoTxt() {
        return arquivoTxt;
    }

    public void setArquivoTxt(File arquivoTxt) {
        this.arquivoTxt = arquivoTxt;
    }

    public int getOcorrencia() {
        return ocorrencia;
    }

    public void setOcorrencia(int ocorrencia) {
        this.ocorrencia = ocorrencia;
    }

    public String printArquivoOcorrencias(){
        return ("arquivo: " + arquivoTxt + "; n° ocorrências: " + ocorrencia);
    }
    
    @Override
    public String toString() {
        return "Palavra{" + "palavra=" + palavra + ", arquivoTxt=" + arquivoTxt + ", ocorrencia=" + ocorrencia + '}';
    }
}
