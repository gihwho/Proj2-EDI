/*  
    GRUPO
    Giovanna Borges Coelho - RA 10756784
    Melissa Namie Shine - RA 10401096

    Link do vídeo: 

    Fontes de consulta:
    https://www.geeksforgeeks.org/linked-list-set-1-introduction/
    https://aartedeprogramar.tech/lista-encadeada-java/
    https://www.bosontreinamentos.com.br/assembly/introducao-a-linguagem-assembly-de-programacao/
    https://exemplosite.com/exemplos-de-codigo-em-assembly-para-entender-sua-aplicacao/?expand_article=1
    https://dev.to/ikauedev/entendendo-arquivos-em-java-um-guia-simples-e-atualizado-52la

*/

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Main {

    private static final BufferedReader BR = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

    private static ProgramaListaEncadeada codigo = new ProgramaListaEncadeada();
    private static String arquivoAtual = null;
    private static boolean modificado = false;

    public static void main(String[] args) throws Exception {
        System.out.println("Interpretador Assembly Simplificado! Digite os comandos... (EXIT para sair)");
        while (true) {
            System.out.print("> ");
            String linha = BR.readLine();
            if (linha == null) break;
            linha = linha.trim();
            if (linha.isEmpty()) continue;

            String upper = linha.toUpperCase();
            try {
                if (upper.startsWith("LOAD ") || upper.equals("LOAD")) {
                    cmdLOAD(linha);
                } else if (upper.equals("LIST")) {
                    cmdLIST();
                } else if (upper.equals("RUN")) {
                    cmdRUN();
                } else if (upper.startsWith("INS ")) {
                    cmdINS(linha);
                } else if (upper.startsWith("DEL ")) {
                    cmdDEL(linha);
                } else if (upper.equals("SAVE")) {
                    cmdSAVE(null);
                } else if (upper.startsWith("SAVE ")) {
                    cmdSAVE(linha.substring(5).trim());
                } else if (upper.equals("EXIT")) {
                    if (!exit()) continue;
                    System.out.println("Fim.");
                    break;
                } else {
                    System.out.println("Erro: comando inválido.");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void cmdLOAD(String linha) throws Exception {
        String[] t = linha.split("\\s+", 2);
        if (t.length < 2) {
            System.out.println("Erro: informe o caminho do arquivo .ed1.");
            return;
        }
        String caminho = t[1].trim();

        if (!podeTrocarArquivo()) return;

        ProgramaListaEncadeada novo = carregarArquivo(caminho);
        if (novo == null) {
            System.out.println("Erro ao abrir o arquivo '" + caminho + "'.");
            return;
        }
        codigo = novo;
        arquivoAtual = caminho;
        modificado = false;
        System.out.println("Arquivo '" + caminho + "' carregado com sucesso.");
    }

    private static void cmdLIST() throws Exception {
        if (codigo.vazia()) {
            System.out.println("(vazio)");
            return;
        }
        final int[] count = {0};
        final int[] page  = {1};

        codigo.listarComCallback((num, inst) -> {
            System.out.println(num + " " + inst);
            count[0]++;
            if (count[0] % 20 == 0) {
                try {
                    System.out.print("-- Página " + page[0] + " (ENTER p/ continuar) --");
                    BR.readLine();
                    page[0]++;
                } catch (IOException e) {
                    // ignora erro na pausa
                }
            }
        });
    }

    private static void cmdRUN() {
        if (codigo.vazia()) {
            System.out.println("Erro: não há código carregado na memória.");
            return;
        }
        new Interpretador().executar(codigo);
    }

    private static void cmdINS(String linha) {
        // INS <LINHA> <INSTRUÇÃO>
        String resto = linha.substring(4).trim();
        int esp = resto.indexOf(' ');
        if (esp < 0) {
            System.out.println("Erro: uso: INS <LINHA> <INSTRUÇÃO>");
            return;
        }

        String sNum  = resto.substring(0, esp).trim();
        String instr = resto.substring(esp + 1).trim();

        if (!ehInteiro(sNum)) {
            System.out.println("Erro: linha " + sNum + " inválida.");
            return;
        }

        int n = Integer.parseInt(sNum);

        // Pega o CONTEÚDO ANTERIOR (string) ANTES de atualizar
        Linha antiga = codigo.buscar(n);
        String instrAntiga = (antiga != null ? antiga.instrucao : null);

        char r = codigo.inserirOuAtualizar(n, instr);
        modificado = true;

        if (r == 'I' || instrAntiga == null) {
            System.out.println("Linha inserida:\n" + n + " " + instr);
        } else {
            // Agora usa a string salva ANTES da atualizacao
            System.out.println("Linha:\n" + n + " " + instrAntiga +
                               "\nAtualizada para:\n" + n + " " + instr);
        }
    }

    private static void cmdDEL(String linha) {
        // DEL <LINHA>  OU  DEL <LI> <LF>
        String resto = linha.substring(4).trim();
        String[] t = resto.split("\\s+");

        if (t.length == 1) {
            if (!ehInteiro(t[0])) {
                System.out.println("Erro: parâmetro inválido.");
                return;
            }
            int n = Integer.parseInt(t[0]);
            Linha rem = codigo.removerLinha(n);
            if (rem == null) {
                System.out.println("Erro: linha " + n + " inexistente.");
            } else {
                System.out.println("Linha removida:\n" + n + " " + rem.instrucao);
                modificado = true;
            }

        } else if (t.length == 2) {
            if (!ehInteiro(t[0]) || !ehInteiro(t[1])) {
                System.out.println("Erro: parâmetros inválidos.");
                return;
            }
            int li = Integer.parseInt(t[0]);
            int lf = Integer.parseInt(t[1]);

            try {
                ProgramaListaEncadeada remov = codigo.removerIntervalo(li, lf);
                if (remov.vazia()) {
                    System.out.println("(nenhuma linha no intervalo)");
                    return;
                }
                System.out.println("Linhas removidas:");
                remov.listarComCallback((num, inst) -> System.out.println(num + " " + inst));
                modificado = true;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }

        } else {
            System.out.println("Erro: uso: DEL <LINHA>  ou  DEL <LI> <LF>");
        }
    }

    private static void cmdSAVE(String caminho) throws Exception {
        if (caminho == null) {
            if (arquivoAtual == null) {
                System.out.println("Erro: informe o nome do arquivo (SAVE <ARQUIVO.ED1>). ");
                return;
            }
            if (salvarArquivo(arquivoAtual)) {
                System.out.println("Arquivo '" + arquivoAtual + "' salvo com sucesso.");
                modificado = false;
            } else {
                System.out.println("Erro ao salvar o arquivo.");
            }
        } else {
            File f = new File(caminho);
            if (f.exists()) {
                System.out.println("Arquivo já existe. Deseja sobrescrever? (S/N)");
                System.out.print("> ");
                String resp = BR.readLine();
                if (resp == null || resp.trim().isEmpty()
                        || !resp.trim().substring(0, 1).equalsIgnoreCase("S")) {
                    System.out.println("Arquivo não salvo.");
                    return;
                }
            }
            if (salvarArquivo(caminho)) {
                arquivoAtual = caminho;
                modificado = false;
                System.out.println("Arquivo '" + caminho + "' salvo com sucesso.");
            } else {
                System.out.println("Erro ao salvar o arquivo.");
            }
        }
    }

    private static ProgramaListaEncadeada carregarArquivo(String caminho) {
        File f = new File(caminho);
        if (!f.exists() || !f.isFile()) return null;

        ProgramaListaEncadeada nova = new ProgramaListaEncadeada();

        try (BufferedReader r = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {

            String line;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                int esp = line.indexOf(' ');
                String sNum = (esp < 0 ? line : line.substring(0, esp)).trim();
                String inst = (esp < 0 ? ""   : line.substring(esp + 1)).trim();

                if (!ehInteiro(sNum)) {
                    System.out.println("Aviso: linha ignorada (número inválido): " + line);
                    continue;
                }
                int numeroLinha = Integer.parseInt(sNum);
                try {
                    nova.inserirOuAtualizar(numeroLinha, inst);
                } catch (Exception e) {
                    System.out.println("Aviso: linha ignorada: " + line + " (" + e.getMessage() + ")");
                }
            }
            return nova;

        } catch (IOException e) {
            return null;
        }
    }

    private static boolean salvarArquivo(String caminho) {
        try (BufferedWriter w = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(caminho), StandardCharsets.UTF_8))) {

            codigo.listarComCallback((num, inst) -> {
                try {
                    w.write(num + " " + inst);
                    w.newLine();
                } catch (IOException ex) {
                    // erro tratado no catch externo
                }
            });
            w.flush();
            return true;

        } catch (IOException e) {
            return false;
        }
    }

    private static boolean podeTrocarArquivo() throws Exception {
        if (!modificado) return true;

        System.out.println(
            (arquivoAtual != null ? "Arquivo atual ('" + arquivoAtual + "')" : "Memória")
            + " contém alterações não salvas.\nDeseja salvar? (S/N)"
        );
        System.out.print("> ");
        String resp = BR.readLine();

        if (resp != null && resp.trim().substring(0, 1).equalsIgnoreCase("S")) {
            if (arquivoAtual == null) {
                System.out.println("Informe um nome para salvar (SAVE <ARQUIVO.ED1>) ou repita o LOAD");
                return false;
            }
            return salvarArquivo(arquivoAtual);
        }
        return true;
    }

    private static boolean exit() throws Exception {
        if (!modificado) return true;

        System.out.println(
            "Arquivo atual ('" + (arquivoAtual == null ? "<sem nome>" : arquivoAtual)
            + "') contém alterações não salvas.\nDeseja salvar? (S/N)"
        );
        System.out.print("> ");
        String resp = BR.readLine();

        if (resp != null && resp.trim().substring(0, 1).equalsIgnoreCase("S")) {
            if (arquivoAtual == null) {
                System.out.println("Erro: informe um nome com SAVE <ARQUIVO.ED1> antes de sair.");
                return false;
            }
            return salvarArquivo(arquivoAtual);
        }
        return true;
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
}