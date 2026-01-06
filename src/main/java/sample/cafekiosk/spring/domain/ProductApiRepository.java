package sample.cafekiosk.spring.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ProductApiRepository {

    private final JPAQueryFactory queryFactory;

    public List<Product> findProductsBySellingStatusIn(List<ProductSellingStatus> sellingStatuses) {
        return queryFactory
                .selectFrom(QProduct.product)
                .where(
                        Qproduct.product.sellingStatus.in(sellingStatuses)
                )
                .fetch();
    }
}
