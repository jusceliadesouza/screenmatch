package br.com.alura.screenmatch.main;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
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

		for(int i=0; i<dados.totalTemporadas(); i++) {
			json = consumoApi.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + (i + 1) + APIKEY);
			DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
			temporadas.add(dadosTemporada);
		}

		temporadas.forEach((System.out::println));

    for (int i=0; i < dados.totalTemporadas(); i++) {
      List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();

      for(int j=0; j < episodiosTemporada.size(); j++) {
        System.out.println(episodiosTemporada.get(j).titulo());
      }
    }

    temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

    // List<String> nomes = List.of("Ana", "Maria", "José");

    // nomes.stream()
    //       .sorted()
    //       .limit(2)
    //       .filter(n -> n.startsWith("J"))
    //       .map(n -> n.toUpperCase())
    //       .forEach(System.out::println);

    List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                                                    .flatMap(t -> t.episodios().stream())
                                                    .collect(Collectors.toList());

    System.out.println("Top 5 episódios: ");
    dadosEpisodios.stream()
                  .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                  .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                  .limit(5)
                  .forEach(System.out::println);
  }
}