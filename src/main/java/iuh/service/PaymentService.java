package iuh.service;

import iuh.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {


    //PayOS SDK
    private final PayOS payOS;

    public CheckoutResponseData createPayment(Order order) throws Exception {

        List<ItemData> items = order.getOrderLines().stream()
                .map(line -> ItemData.builder()
                        .name(line.getProduct().getName())
                        .quantity(line.getAmount())
                        .price(line.getProduct().getPrice().intValue())
                        .build()
                )
                .collect(Collectors.toList());


        BigDecimal totalAmount = order.getOrderLines().stream()
                .map(line -> line.getProduct().getPrice().multiply(BigDecimal.valueOf(line.getAmount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        PaymentData paymentData = PaymentData.builder()
                .orderCode(Long.valueOf(order.getId()))
                .amount(totalAmount.intValue())
                .description("Payment for Order #+" + order.getId())
                .items(items)
                .returnUrl("http://localhost:8080/cart/payment-success?orderId=" + order.getId())
                .cancelUrl("http://localhost:8080/cart/payment-cancel?orderId=" + order.getId())
                .build();

        return payOS.createPaymentLink(paymentData);
    }

}
