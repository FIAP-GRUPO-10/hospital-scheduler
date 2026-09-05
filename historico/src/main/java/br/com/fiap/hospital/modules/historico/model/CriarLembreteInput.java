package br.com.fiap.hospital.modules.historico.model;

public class CriarLembreteInput {
    private String pacienteId;
    private String medicoId;
    private String dataConsulta;
    private String mensagem;

    public CriarLembreteInput() {}

    public CriarLembreteInput(String pacienteId, String medicoId, String dataConsulta, String mensagem) {
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.dataConsulta = dataConsulta;
        this.mensagem = mensagem;
    }

    public String getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(String pacienteId) {
        this.pacienteId = pacienteId;
    }

    public String getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(String medicoId) {
        this.medicoId = medicoId;
    }

    public String getDataConsulta() {
        return dataConsulta;
    }

    public void setDataConsulta(String dataConsulta) {
        this.dataConsulta = dataConsulta;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
