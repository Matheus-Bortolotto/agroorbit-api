package com.agroorbit.api.soap;

import com.agroorbit.api.dao.FazendaDAO;
import com.agroorbit.api.model.Fazenda;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Optional;

@Slf4j
@Endpoint
@RequiredArgsConstructor
public class FazendaEndpoint {

    private static final String NAMESPACE = "http://agroorbit.com/soap";

    private final FazendaDAO fazendaDAO;

    @PayloadRoot(namespace = NAMESPACE, localPart = "consultarFazendaRequest")
    @ResponsePayload
    public ConsultarFazendaResponse consultarFazenda(@RequestPayload ConsultarFazendaRequest request) {
        log.info("[SOAP] consultarFazenda — email: {}", request.getEmail());

        ConsultarFazendaResponse response = new ConsultarFazendaResponse();
        Optional<Fazenda> fazenda = fazendaDAO.findByEmail(request.getEmail());

        if (fazenda.isPresent()) {
            Fazenda f = fazenda.get();
            response.setId(f.getId());
            response.setNome(f.getNome());
            response.setProprietario(f.getProprietario());
            response.setEmail(f.getEmail());
            response.setEstado(f.getEstado());
            response.setMunicipio(f.getMunicipio());
            response.setCulturaPlantada(f.getCulturaPlantada());
            response.setAreaHectares(f.getAreaHectares());
            response.setSucesso(true);
            response.setMensagem("Fazenda encontrada com sucesso.");
        } else {
            response.setSucesso(false);
            response.setMensagem("Fazenda não encontrada para o email: " + request.getEmail());
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "registrarAlertaRequest")
    @ResponsePayload
    public RegistrarAlertaResponse registrarAlerta(@RequestPayload RegistrarAlertaRequest request) {
        log.info("[SOAP] registrarAlerta — fazenda: {}", request.getEmailFazenda());

        double ndvi = request.getIndiceNDVI();
        double umidade = request.getUmidadeSolo();
        double temp = request.getTemperaturaMedia();

        String nivelRisco;
        int scoreRisco;
        String recomendacao;

        if (ndvi < 0.2 || umidade < 15.0 || temp > 40.0) {
            nivelRisco = "CRITICO";
            scoreRisco = (int) (100 - (ndvi * 50) - (umidade * 0.3));
            recomendacao = "URGENTE: Iniciar irrigação de emergência imediatamente.";
        } else if (ndvi < 0.4 || umidade < 30.0 || temp > 35.0) {
            nivelRisco = "ALERTA";
            scoreRisco = (int) (70 - (ndvi * 40) - (umidade * 0.2));
            recomendacao = "ATENÇÃO: Monitorar nos próximos 3 dias e preparar irrigação.";
        } else {
            nivelRisco = "NORMAL";
            scoreRisco = (int) (30 - (ndvi * 20));
            recomendacao = "Lavoura dentro dos parâmetros normais.";
        }

        RegistrarAlertaResponse response = new RegistrarAlertaResponse();
        response.setNivelRisco(nivelRisco);
        response.setScoreRisco(scoreRisco);
        response.setRecomendacao(recomendacao);
        response.setSucesso(true);
        response.setMensagem("Alerta processado via SOAP com sucesso.");
        return response;
    }

    // ── Classes internas ──

    @XmlRootElement(namespace = NAMESPACE, name = "consultarFazendaRequest")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConsultarFazendaRequest {
        @XmlElement(required = true) private String email;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    @XmlRootElement(namespace = NAMESPACE, name = "consultarFazendaResponse")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConsultarFazendaResponse {
        private Long id;
        private String nome;
        private String proprietario;
        private String email;
        private String estado;
        private String municipio;
        private String culturaPlantada;
        private Double areaHectares;
        private String mensagem;
        private boolean sucesso;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getProprietario() { return proprietario; }
        public void setProprietario(String p) { this.proprietario = p; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
        public String getMunicipio() { return municipio; }
        public void setMunicipio(String municipio) { this.municipio = municipio; }
        public String getCulturaPlantada() { return culturaPlantada; }
        public void setCulturaPlantada(String c) { this.culturaPlantada = c; }
        public Double getAreaHectares() { return areaHectares; }
        public void setAreaHectares(Double a) { this.areaHectares = a; }
        public String getMensagem() { return mensagem; }
        public void setMensagem(String mensagem) { this.mensagem = mensagem; }
        public boolean isSucesso() { return sucesso; }
        public void setSucesso(boolean sucesso) { this.sucesso = sucesso; }
    }

    @XmlRootElement(namespace = NAMESPACE, name = "registrarAlertaRequest")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class RegistrarAlertaRequest {
        @XmlElement(required = true) private String emailFazenda;
        @XmlElement(required = true) private double indiceNDVI;
        @XmlElement(required = true) private double temperaturaMedia;
        @XmlElement(required = true) private double umidadeSolo;
        public String getEmailFazenda() { return emailFazenda; }
        public void setEmailFazenda(String e) { this.emailFazenda = e; }
        public double getIndiceNDVI() { return indiceNDVI; }
        public void setIndiceNDVI(double v) { this.indiceNDVI = v; }
        public double getTemperaturaMedia() { return temperaturaMedia; }
        public void setTemperaturaMedia(double v) { this.temperaturaMedia = v; }
        public double getUmidadeSolo() { return umidadeSolo; }
        public void setUmidadeSolo(double v) { this.umidadeSolo = v; }
    }

    @XmlRootElement(namespace = NAMESPACE, name = "registrarAlertaResponse")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class RegistrarAlertaResponse {
        private String nivelRisco;
        private int scoreRisco;
        private String recomendacao;
        private boolean sucesso;
        private String mensagem;
        public String getNivelRisco() { return nivelRisco; }
        public void setNivelRisco(String v) { this.nivelRisco = v; }
        public int getScoreRisco() { return scoreRisco; }
        public void setScoreRisco(int v) { this.scoreRisco = v; }
        public String getRecomendacao() { return recomendacao; }
        public void setRecomendacao(String v) { this.recomendacao = v; }
        public boolean isSucesso() { return sucesso; }
        public void setSucesso(boolean v) { this.sucesso = v; }
        public String getMensagem() { return mensagem; }
        public void setMensagem(String v) { this.mensagem = v; }
    }
}