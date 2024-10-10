package com.adinuegroho;

import com.adinuegroho.webclient.CustomerClient;
import com.adinuegroho.webclient.ProductClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@SpringBootApplication
@EnableDiscoveryClient // Add this annotation to enable service registration and discovery
public class OrderServiceNewApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceNewApplication.class, args);
	}

	@Autowired
	private LoadBalancedExchangeFilterFunction loadBalancedExchangeFilterFunction;

	@Autowired
	private WebClient.Builder webClientBuilderCustomer;

	@Autowired
	private WebClient.Builder webClientBuilderProduct;

	@Bean
	WebClient webClientCustomer() {
		return webClientBuilderCustomer
				.baseUrl("http://customer-service")
				.filter(loadBalancedExchangeFilterFunction)
				.build();
	}

	@Bean
	WebClient webClientProduct() {
		return webClientBuilderProduct
				.baseUrl("http://product-service")
				.filter(loadBalancedExchangeFilterFunction)
				.build();
	}

	@Bean
	CustomerClient customerClient() {
		HttpServiceProxyFactory factory = HttpServiceProxyFactory
				.builder(WebClientAdapter.forClient(webClientCustomer())).build();

		return factory.createClient(CustomerClient.class);
	}

	@Bean
	ProductClient productClient() {
		HttpServiceProxyFactory factory = HttpServiceProxyFactory
				.builder(WebClientAdapter.forClient(webClientProduct())).build();

		return factory.createClient(ProductClient.class);
	}

}
