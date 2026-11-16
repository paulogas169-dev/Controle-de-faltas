package Escola;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class Frequencia {

    private int id;
    private int alunoId;
    private Date data;
    private Boolean presente;
    private Timestamp entradaHora;
    private Timestamp saidaHora;
    private String alunoNome;
    private String salaNome;
    private int totalFaltasAluno;

    public Frequencia() {
    }

    public Frequencia(int id, int alunoId, Date data, Boolean presente) {
        this.id = id;
        this.alunoId = alunoId;
        this.data = data;
        this.presente = presente;
    }

    public Frequencia(int id, int alunoId, Date data, Boolean presente, String alunoNome, String salaNome) {
        this.id = id;
        this.alunoId = alunoId;
        this.data = data;
        this.presente = presente;
        this.alunoNome = alunoNome;
        this.salaNome = salaNome;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAlunoId() {
        return alunoId;
    }

    public void setAlunoId(int alunoId) {
        this.alunoId = alunoId;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Boolean isPresente() {
        return presente;
    }

    public void setPresente(Boolean presente) {
        this.presente = presente;
    }

    public Timestamp getEntradaHora() {
        return entradaHora;
    }

    public void setEntradaHora(Timestamp entradaHora) {
        this.entradaHora = entradaHora;
    }

    public String getEntradaHoraFormatada() {
        if (entradaHora == null) {
            return "—";
        }
        return entradaHora.toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public Timestamp getSaidaHora() {
        return saidaHora;
    }

    public void setSaidaHora(Timestamp saidaHora) {
        this.saidaHora = saidaHora;
    }

    public String getSaidaHoraFormatada() {
        if (saidaHora == null) {
            return "—";
        }
        return saidaHora.toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public String getAlunoNome() {
        return alunoNome;
    }

    public void setAlunoNome(String alunoNome) {
        this.alunoNome = alunoNome;
    }

    public String getSalaNome() {
        return salaNome;
    }

    public void setSalaNome(String salaNome) {
        this.salaNome = salaNome;
    }

    public int getTotalFaltasAluno() {
        return totalFaltasAluno;
    }

    public void setTotalFaltasAluno(int totalFaltasAluno) {
        this.totalFaltasAluno = totalFaltasAluno;
    }
}

