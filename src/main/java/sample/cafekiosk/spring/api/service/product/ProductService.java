package sample.cafekiosk.spring.api.service.product;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sample.cafekiosk.spring.api.controller.product.request.ProductCreateRequest;
import sample.cafekiosk.spring.api.service.product.request.ProductCreateServiceRequest;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductResponse> getSellingProducts() {
        List<Product> products = productRepository.findAllBySellingTypeIn(ProductSellingStatus.forDisplay());

        return products.stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse createProduct(ProductCreateServiceRequest request) {
        // productNumber : DB에서 마지막 저장된 Product의 상품번호를 읽어와서 +1 ex : 009 -> 010
        String latestProductNumber = createNextProductNumber();

        // DB 저장
        Product product = request.toEntity(latestProductNumber);
        Product savedProduct = productRepository.save(product);

        // ProductResponse build
        return ProductResponse.of(savedProduct);
    }

    private String createNextProductNumber() {
        String latestProductNumber =  productRepository.findLatestProductNumber();
        if(latestProductNumber == null) return "001";
        int latestProductNumberInt = Integer.parseInt(latestProductNumber);

        int nextProductNumberInt = latestProductNumberInt + 1;

        return String.format("%03d",  nextProductNumberInt);
    }
}
