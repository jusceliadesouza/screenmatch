package br.com.alura.screenmatch.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
    temporadas.forEach(System.out::println);
  }
}