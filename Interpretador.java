/*  
    GRUPO
    Giovanna Borges Coelho - RA 10756784
    Melissa Namie Shine - RA 10401096

    Descrição: maquina que executa o programa em memoria
*/

class Interpretador {
    private final int[] regs = new int[26];
    private final boolean[] definido = new boolean[26];

    private static int idxReg(char c) {
        return Character.toUpperCase(c) - 'A';
    }

    private static boolean ehRegistrador(String s) {
        if (s == null || s.length() != 1) return false;
        char c = s.charAt(0);
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    private static boolean ehInteiro(String s) {
        if (s == null || s.isEmpty()) return false;
        int i = 0;
        if (s.charAt(0) == '-' || s.charAt(0) == '+') i = 1;
        if (i == s.length()) return false;
        for (; i < s.length(); i++) {
            if (s.charAt(i) < '0' || s.charAt(i) > '9') return false;
        }
        return true;
    }

    //le um valor que pode ser inteiro OU registrador (ja definido)
    private int lerValor(String token) throws Exception {
        if (ehRegistrador(token)) {
            int k = idxReg(token.charAt(0));
            if (!definido[k]) {
                throw new Exception("Erro: registrador " +
                                    Character.toUpperCase(token.charAt(0)) +
                                    " inválido.");
            }
            return regs[k];
        } else if (ehInteiro(token)) {
            return Integer.parseInt(token);
        } else {
            throw new Exception("Erro: operando inválido: " + token);
        }
    }

    //garante que o registrador existe e ja foi definido (usado em leituras)
    private void exigirRegistradorDefinidoParaLeitura(String x, String linhaBruta) throws Exception {
        if (!ehRegistrador(x)) {
            throw new Exception("Erro: operando inválido.");
        }
        int k = idxReg(x.charAt(0));
        if (!definido[k]) {
            throw new Exception("Erro: registrador " +
                                Character.toUpperCase(x.charAt(0)) +
                                " inválido.");
        }
    }

    //atribui um valor a um registrador (x deve ser registrador valido)
    private void atribuir(String x, int valor) throws Exception {
        if (!ehRegistrador(x)) {
            throw new Exception("Erro: operando inválido: " + x);
        }
        int k = idxReg(x.charAt(0));
        regs[k] = valor;
        definido[k] = true;
    }

    // ATENÇÃO: troque o tipo do parametro se sua classe for ProgramaLista em vez de ProgramaListaEncadeada
    public void executar(ProgramaListaEncadeada codigo) {
        if (codigo == null || codigo.vazia()) {
            System.out.println("Erro: não há código carregado na memória.");
            return;
        }

        // Copia a lista encadeada para vetores (numero da linha + instrucao)
        int n = codigo.tamanho();
        int[] linhas = new int[n];
        String[] insts = new String[n];
        final int[] idx = {0};

        codigo.listarComCallback((num, inst) -> {
            linhas[idx[0]] = num;
            insts[idx[0]] = inst;
            idx[0]++;
        });

        int pc = 0; // program counter = indice no vetor

        while (pc >= 0 && pc < n) {
            String instrucao = insts[pc];
            String linhaBruta = linhas[pc] + " " + instrucao;

            String[] t = instrucao.trim().split("\\s+");
            if (t.length == 0 || t[0].isEmpty()) {
                pc++;
                continue;
            }

            String op = t[0].toLowerCase();

            try {
                switch (op) {
                    case "mov": {
                        if (t.length != 3) {
                            throw new Exception("Erro: MOV requer 2 argumentos.");
                        }
                        atribuir(t[1], lerValor(t[2]));
                        pc++;
                        break;
                    }
                    case "inc": {
                        if (t.length != 2) {
                            throw new Exception("Erro: INC requer 1 argumento.");
                        }
                        exigirRegistradorDefinidoParaLeitura(t[1], linhaBruta);
                        int k = idxReg(t[1].charAt(0));
                        regs[k]++;
                        pc++;
                        break;
                    }
                    case "dec": {
                        if (t.length != 2) {
                            throw new Exception("Erro: DEC requer 1 argumento.");
                        }
                        exigirRegistradorDefinidoParaLeitura(t[1], linhaBruta);
                        int k = idxReg(t[1].charAt(0));
                        regs[k]--;
                        pc++;
                        break;
                    }
                    case "add": {
                        if (t.length != 3) {
                            throw new Exception("Erro: ADD requer 2 argumentos.");
                        }
                        exigirRegistradorDefinidoParaLeitura(t[1], linhaBruta);
                        int xk = idxReg(t[1].charAt(0));
                        int yv = lerValor(t[2]);
                        regs[xk] = regs[xk] + yv;
                        pc++;
                        break;
                    }
                    case "sub": {
                        if (t.length != 3) {
                            throw new Exception("Erro: SUB requer 2 argumentos.");
                        }
                        exigirRegistradorDefinidoParaLeitura(t[1], linhaBruta);
                        int xk = idxReg(t[1].charAt(0));
                        int yv = lerValor(t[2]);
                        regs[xk] = regs[xk] - yv;
                        pc++;
                        break;
                    }
                    case "mul": {
                        if (t.length != 3) {
                            throw new Exception("Erro: MUL requer 2 argumentos.");
                        }
                        exigirRegistradorDefinidoParaLeitura(t[1], linhaBruta);
                        int xk = idxReg(t[1].charAt(0));
                        int yv = lerValor(t[2]);
                        regs[xk] = regs[xk] * yv;
                        pc++;
                        break;
                    }
                    case "div": {
                        if (t.length != 3) {
                            throw new Exception("Erro: DIV requer 2 argumentos.");
                        }
                        exigirRegistradorDefinidoParaLeitura(t[1], linhaBruta);
                        int xk = idxReg(t[1].charAt(0));
                        int yv = lerValor(t[2]);
                        if (yv == 0) {
                            throw new Exception("Erro: divisão por zero.");
                        }
                        regs[xk] = regs[xk] / yv;
                        pc++;
                        break;
                    }
                    case "jnz": {
                        if (t.length != 3) {
                            throw new Exception("Erro: JNZ requer 2 argumentos.");
                        }
                        // x precisa ser registrador valido e definido
                        exigirRegistradorDefinidoParaLeitura(t[1], linhaBruta);

                        // y pode ser inteiro OU registrador contendo o numero da linha
                        int alvo = lerValor(t[2]);

                        // verifica se existe linha com esse numero
                        int novoPc = -1;
                        for (int i = 0; i < n; i++) {
                            if (linhas[i] == alvo) {
                                novoPc = i;
                                break;
                            }
                        }
                        if (novoPc == -1) {
                            throw new Exception("Erro: linha " + alvo + " não existe no código.");
                        }

                        int k = idxReg(t[1].charAt(0));
                        if (regs[k] != 0) {
                            pc = novoPc;
                        } else {
                            pc++;
                        }
                        break;
                    }
                    case "out": {
                        if (t.length != 2) {
                            throw new Exception("Erro: OUT requer 1 argumento.");
                        }
                        exigirRegistradorDefinidoParaLeitura(t[1], linhaBruta);
                        int k = idxReg(t[1].charAt(0));
                        System.out.println(regs[k]);
                        pc++;
                        break;
                    }
                    default:
                        throw new Exception("Erro: instrução desconhecida: " + t[0]);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("Linha: " + linhaBruta);
                pc++;
            }
        }
    }
}