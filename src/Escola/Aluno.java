package Escola;

public class Aluno {
    private int id;
    private String nome;
    private int faltas;
    private int total_faltas;
    private int sala_id;
    private String sala;
    private String uid_rfid;

    public String getUid_rfid() {
        return uid_rfid;
    }

    public void setUid_rfid(String uid_rfid) {
        this.uid_rfid = uid_rfid;
    }

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }

    public Aluno() {

    }

    public Aluno(int id, String nome, int faltas, int total_faltas, int sala_id) {
        this.id = id;
        this.nome = nome;
        this.faltas = faltas;
        this.total_faltas = total_faltas;
        this.sala_id = sala_id;
    }

    public Aluno(int id, String nome, String uid_rfid, int sala_id) {
        this.id = id;
        this.nome = nome;
        this.uid_rfid = uid_rfid;
        this.sala_id = sala_id;
    }

    @Override
    public String toString() {
        if (sala != null && !sala.isBlank()) {
            return nome + " (" + sala + ")";
        }
        return nome != null ? nome : "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getFaltas() {
        return faltas;
    }

    public void setFaltas(int faltas) {
        this.faltas = faltas;
    }

    public int getTotal_faltas() {
        return total_faltas;
    }

    public void setTotal_faltas(int total_faltas) {
        this.total_faltas = total_faltas;
    }

    public int getSala_id() {
        return sala_id;
    }

    public void setSala_id(int sala_id) {
        this.sala_id = sala_id;
    }
}
