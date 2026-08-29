package br.com.fiap.hospital.modules.notificacoes.constants;

public class NotificacaoConstants {
    // Usuários de teste
    public static final String PACIENTE1_ID = "PAC-1001";
    public static final String PACIENTE2_ID = "PAC-1002";
    public static final String MEDICO1_ID = "MED-2001";
    public static final String MEDICO2_ID = "MED-2002";

    // Mensagens de lembrete
    public static final String MSG_LEMBRETE_AMANHA = 
        "Lembrete: sua consulta está agendada para amanhã às 09:00.";
    public static final String MSG_LEMBRETE_CONFIRMACAO = 
        "Lembrete: sua consulta do dia 2 está confirmada.";

    // Roles
    public static final String ROLE_MEDICO = "ROLE_MEDICO";
    public static final String ROLE_ENFERMEIRO = "ROLE_ENFERMEIRO";
    public static final String ROLE_PACIENTE = "ROLE_PACIENTE";

    // Mensagens de erro
    public static final String ERRO_PACIENTE_OBRIGATORIO = "Paciente obrigatório.";
    public static final String ERRO_MEDICO_OBRIGATORIO = "Médico obrigatório.";
    public static final String ERRO_DATA_OBRIGATORIA = "Data da consulta obrigatória.";
    public static final String ERRO_MSG_OBRIGATORIA = "Mensagem do lembrete obrigatória.";
    public static final String ERRO_PERFIL_INVALIDO = 
        "Apenas médicos e enfermeiros podem disparar lembretes.";
}
