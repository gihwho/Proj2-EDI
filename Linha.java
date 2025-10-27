/*  
    GRUPO
    Giovanna Borges Coelho - RA 10756784
    Melissa Namie Shine - RA

    Descrição: representa uma linha do programa Assembly na memoria
*/

public class Linha {
    int numeroLinha;   // 10, 20, 30
    String instrucao;  // mov a 5

    Linha(int n, String i) {
        this.numeroLinha = n; this.instrucao = i;
    }
}
