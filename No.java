/*  
    GRUPO
    Giovanna Borges Coelho - RA 10756784
    Melissa Namie Shine - RA 10401096

    Descrição: Implementa um nó genérico para listas encadeadas
*/

public class No<T> {
    T info;
    No<T> prox;

    No(T info) { 
        this.info = info;
    }
}
