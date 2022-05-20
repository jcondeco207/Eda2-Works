//----------------------------------------------------------//
// 1º Trabalho de E.D.A. 2 (Problem: Mosaics)

// Engenharia Informática, Universidade de Évora
// Joana Carrasqueira (nº 48566) e João Condeço (nº 48976)
// Março de 2022
//----------------------------------------------------------//

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main{
    public static void main(String[] args) throws NumberFormatException, IOException{
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        
        //Dimensões da tabela
        String cl[] = (input.readLine()).split(" ");
        int lines = Integer.parseInt(cl[0]);
        int cols = Integer.parseInt(cl[1]);
        long lineComb = 1;

        //Leitura da tabela
        for(int i = 0; i < lines; i++){
            long calc = 1;
            String line = input.readLine();
            int spaces = 0;
            for(int k = 0; k < cols; k++){
                //Tratamento da linha 
                if(line.charAt(k) == '.'){                                                              // => Se aparecer pontos ignora e continua
                    spaces = 0;
                    continue;
                }
                else if(((k+1) == cols) || (line.charAt(k + 1) != line.charAt(k))){                     //  => Se a letra seguinte não for igual à atual
                    Combs ncombs = new Combs(spaces + 1);                                               //  => Crio o objeto 
                    calc *= ncombs.combos(spaces + 1);                                                  //  => Calculo as combinações e combino com as atuais
                    spaces = 0;
                    continue;                                                                           //  => A nova letra 
                }
                spaces++;
            }
            lineComb *= calc;                                                                           //  => Calcula o total de combinações tendo em conta todas as linhas da tabela
        }

        input.close();
        System.out.println(lineComb);

    }

}

class Combs{
    final private int pieces[] = {1 , 2, 3, 4, 6, 8, 10, 12, 16};       //  => Conjunto das peças possíveis 
    private long mem[];                                                 //  => Array que guarda número de combinações possiveis para N espaços

    public Combs(int spaces){
        this.mem = new long[spaces + 1];                                //  => Inicializa o array para o número de espaços em causa
        this.mem[0] = 1;                                                //  => O caso base (0) é inicializado a 1 e os restantes a 0
        for(int i = 1; i < this.mem.length; i++){
            this.mem[i] = 0;
        }
    }

    public long combos(int spaces){
        long ncombs = 0;                                                //  => Variável que guarda o número de combinações para o espaço indicado
        if(spaces == 0){                                                //  => Se igual a 0, trata-se do caso base e retorna 1
            return 1;
        }
        else if(this.mem[spaces] != 0){                                 //  => Se o valor na sua posição no array for diferente de 0 então o valor já foi calculado e retira-se do array
            return this.mem[spaces];
        } 
        else{                                                           //  => Em último caso vai calcular as combinações possiveis
            for(int i = 0; i < this.pieces.length; i++){
                if(this.pieces[i] <= spaces){                           //  => Só tem em causa as peças que caibam no espaço disponivel
                    ncombs += combos(spaces - this.pieces[i]);          //  => Efetua o mesmo procedimento para o número de espaços sem a peça em questão e soma ao nº de combinações atuais
                    this.mem[spaces] = ncombs;                          //  => Armazena no array o valor
                }
            }
        }
        return ncombs;
    }
}