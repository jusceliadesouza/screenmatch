package br.com.alura.screenmatch.model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Episodio {
  private Integer temperada;
  private String titulo;
  private Integer numeroEpisodio;
  private Double avaliacao;
  private LocalDate dataLancamento;

  public Episodio(Integer numeroTemporada, DadosEpisodio dadosEpisodio) {
    this.temperada = numeroTemporada;
    this.titulo = dadosEpisodio.titulo();
    this.numeroEpisodio = dadosEpisodio.numero();

    try {
      this.avaliacao = Double.valueOf(dadosEpisodio.avaliacao());
    } catch (NumberFormatException ex) {
      this.avaliacao = 0.0;
    }

    try {
      this.dataLancamento = LocalDate.parse(dadosEpisodio.dataLancamento());
    } catch (DateTimeParseException ex) {
      this.dataLancamento = null;
    }
  }

  public Integer getTemperada() {
    return temperada;
  }

  public void setTemperada(Integer temperada) {
    this.temperada = temperada;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public Integer getNumeroEpisodio() {
    return numeroEpisodio;
  }

  public void setNumeroEpisodio(Integer numeroEpisodio) {
    this.numeroEpisodio = numeroEpisodio;
  }

  public Double getAvaliacao() {
    return avaliacao;
  }

  public void setAvaliacao(Double avaliacao) {
    this.avaliacao = avaliacao;
  }

  public LocalDate getDataLancamento() {
    return dataLancamento;
  }

  public void setDataLancamento(LocalDate dataLancamento) {
    this.dataLancamento = dataLancamento;
  }

  @Override
  public String toString() {
    return "temporada = " + temperada +
        ", titulo = '" + titulo + '\'' +
        ", numeroEpisodio = " + numeroEpisodio +
        ", avaliacao = " + avaliacao +
        ", dataLancamento = " + dataLancamento;
  }
}
