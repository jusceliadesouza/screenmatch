package br.com.alura.screenmatch.main;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.Map; // Add this line
import java.util.DoubleSummaryStatistics; // Add this line

import org.springframework.boot.context.config.ConfigData.Option;
import org.springframework.boot.origin.SystemEnvironmentOrigin;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

public class Principal {
  private Scanner scanner = new Scanner(System.in);
  private ConsumoApi consumoApi = new ConsumoApi();
  private ConverteDados conversor = new ConverteDados();

  private final String ENDERECO = "https://www.omdbapi.com/?t=";
  private final String APIKEY = "&apikey=6585022c";

  public void exibeMenu() {
    System.out.println("Digite o nome da série: ");

    var nomeSerie = scanner.nextLine();

    var json = consumoApi.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + APIKEY);

    DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
    System.out.println(dados);

    List<DadosTemporada> temporadas = new ArrayList<>();

    for (int i = 0; i < dados.totalTemporadas(); i++) {
      json = consumoApi.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + (i + 1) + APIKEY);
      DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
      temporadas.add(dadosTemporada);
    }

    temporadas.forEach((System.out::println));

    for (int i = 0; i < dados.totalTemporadas(); i++) {
      List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();

      for (int j = 0; j < episodiosTemporada.size(); j++) {
        System.out.println(episodiosTemporada.get(j).titulo());
      }
    }

    temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

    List<DadosEpisodio> dadosEpisodios = temporadas.stream()
        .flatMap(t -> t.episodios().stream())
        .collect(Collectors.toList());

    System.out.println("Top 10 episódios: ");
    dadosEpisodios.stream()
        .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
        .peek(e -> System.out.println("Primeiro filtro (N/A) " + e))
        .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
        .peek(e -> System.out.println("Ordenação " + e))
        .limit(10)
        .peek(e -> System.out.println("Limite " + e))
        .map(e -> e.titulo().toUpperCase())
        .peek(e -> System.out.println("Mapeamento " + e))
        .forEach(System.out::println);

    List<Episodio> episodios = temporadas.stream()
        .flatMap(t -> t.episodios().stream()
            .map(d -> new Episodio(t.numero(), d)))
        .collect(Collectors.toList());
    episodios.forEach(System.out::println);

    // System.out.println("Digite o trecho do título que deseja buscar: ");
    // var trechoTitulo = scanner.nextLine();

    // Optional<Episodio> episodioBuscado = episodios.stream()
    // .filter(e ->
    // e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
    // .findFirst();

    // if (episodioBuscado.isPresent()) {
    // System.out.println("Episódio encontrado: " +
    // episodioBuscado.get().getTemperada());
    // } else {
    // System.out.println("Episódio não encontrado");
    // }

    // System.out.println("A partir de qual ano deseja assistir?");
    // var ano = scanner.nextInt();

    // LocalDate dataBusca = LocalDate.of(ano, 1, 1);

    // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // episodios.stream()
    // .filter(e -> e.getDataLancamento() != null &&
    // e.getDataLancamento().isAfter(dataBusca))
    // .forEach(e -> {
    // System.out.println("Temporada: " + e.getTemperada() + " Espisódio: " +
    // e.getTitulo() + " Data de Lançamento: " +
    // e.getDataLancamento().format(formatter));
    // });

    Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
        .filter(e -> e.getAvaliacao() != 0.0)
        .collect(Collectors.groupingBy(Episodio::getTemperada, Collectors.averagingDouble(Episodio::getAvaliacao)));

    System.out.println(avaliacoesPorTemporada);

    DoubleSummaryStatistics stats = episodios.stream()
        .filter(e -> e.getAvaliacao() > 0.0)
        .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));

    System.out.println("Média: " + stats.getAverage());
    System.out.println("Mínimo: " + stats.getMin());
    System.out.println("Máximo: " + stats.getMax());
    System.out.println("Quantidade: " + stats.getCount());
  }
}