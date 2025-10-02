import java.util.*;

public class SimuladorFilas {

    static Random rand = new Random();
    static final int N_EVENTOS = 100_000;

    // distribuições 
    static double chegada() { return 2 + (4 - 2) * rand.nextDouble(); }
    static double servico1() { return 1 + (2 - 1) * rand.nextDouble(); }
    static double servico2() { return 4 + (6 - 4) * rand.nextDouble(); }
    static double servico3() { return 5 + (15 - 5) * rand.nextDouble(); }

    // limites das filas
    static final int capFila2 = 5;
    static final int capFila3 = 10;

    // estatistica
    static double tempoTotal = 0;
    static int perdasFila2 = 0;
    static int perdasFila3 = 0;

    static double tempoOcupado1 = 0;
    static double tempoOcupado2 = 0;
    static double tempoOcupado3 = 0;

    // estrutura de evento
    static class Evento implements Comparable<Evento> {
        double tempo;
        String tipo;
        int idCliente;
        int fila;

        Evento(double t, String tp, int id, int f) {
            tempo = t; tipo = tp; idCliente = id; fila = f;
        }

        @Override
        public int compareTo(Evento o) {
            return Double.compare(this.tempo, o.tempo);
        }
    }

    public static void main(String[] args) {
        PriorityQueue<Evento> eventos = new PriorityQueue<>();
        eventos.add(new Evento(2.0, "chegada", 0, 1)); 

        Queue<Integer> fila1 = new LinkedList<>();
        Queue<Integer> fila2 = new LinkedList<>();
        Queue<Integer> fila3 = new LinkedList<>();

        int servidores1 = 0, servidores2 = 0, servidores3 = 0;
        int idCliente = 1;
        int eventosProcessados = 0;

        while (!eventos.isEmpty() && eventosProcessados < N_EVENTOS) {
            Evento ev = eventos.poll();
            tempoTotal = Math.max(tempoTotal, ev.tempo);
            eventosProcessados++;

            if (ev.tipo.equals("chegada")) {
                if (ev.fila == 1) {
                    if (servidores1 == 0) {
                        servidores1 = 1;
                        eventos.add(new Evento(ev.tempo + servico1(), "saida", ev.idCliente, 1));
                    } else {
                        fila1.add(ev.idCliente);
                    }
                
                    eventos.add(new Evento(ev.tempo + chegada(), "chegada", idCliente++, 1));
                }
                else if (ev.fila == 2) {
                    if (servidores2 < 2) {
                        servidores2++;
                        eventos.add(new Evento(ev.tempo + servico2(), "saida", ev.idCliente, 2));
                    } else if (fila2.size() < capFila2) {
                        fila2.add(ev.idCliente);
                    } else {
                        perdasFila2++;
                    }
                }
                else if (ev.fila == 3) {
                    if (servidores3 < 2) {
                        servidores3++;
                        eventos.add(new Evento(ev.tempo + servico3(), "saida", ev.idCliente, 3));
                    } else if (fila3.size() < capFila3) {
                        fila3.add(ev.idCliente);
                    } else {
                        perdasFila3++;
                    }
                }
            }
            else if (ev.tipo.equals("saida")) {
                if (ev.fila == 1) {
                    tempoOcupado1 += (ev.tempo);
                    servidores1 = 0;
                    if (!fila1.isEmpty()) {
                        int cid2 = fila1.poll();
                        servidores1 = 1;
                        eventos.add(new Evento(ev.tempo + servico1(), "saida", cid2, 1));
                    }
                    int destino = (rand.nextDouble() < 0.8) ? 2 : 3;
                    eventos.add(new Evento(ev.tempo, "chegada", ev.idCliente, destino));
                }
                else if (ev.fila == 2) {
                    tempoOcupado2 += (ev.tempo);
                    servidores2--;
                    if (!fila2.isEmpty()) {
                        int cid2 = fila2.poll();
                        servidores2++;
                        eventos.add(new Evento(ev.tempo + servico2(), "saida", cid2, 2));
                    }
                    double r = rand.nextDouble();
                    if (r < 0.3) {
                        eventos.add(new Evento(ev.tempo, "chegada", ev.idCliente, 1));
                    } else if (r < 0.8) {
                        eventos.add(new Evento(ev.tempo, "chegada", ev.idCliente, 3));
                    }
                }
                else if (ev.fila == 3) {
                    tempoOcupado3 += (ev.tempo);
                    servidores3--;
                    if (!fila3.isEmpty()) {
                        int cid2 = fila3.poll();
                        servidores3++;
                        eventos.add(new Evento(ev.tempo + servico3(), "saida", cid2, 3));
                    }
                    if (rand.nextDouble() < 0.7) {
                        eventos.add(new Evento(ev.tempo, "chegada", ev.idCliente, 2));
                    }
                }
            }
        }

        // resultado
        System.out.println("Fila 1 - Tempo acumulado de ocupação: " + tempoOcupado1);
        System.out.println("Fila 2 - Tempo acumulado de ocupação: " + tempoOcupado2 + " | Perdas: " + perdasFila2);
        System.out.println("Fila 3 - Tempo acumulado de ocupação: " + tempoOcupado3 + " | Perdas: " + perdasFila3);
        System.out.println("Tempo total da simulação: " + tempoTotal);
    }
}
