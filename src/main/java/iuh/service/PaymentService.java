package iuh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PayOS payOS;

    public CheckoutResponseData createPayment(String orderId, Integer quantity, Integer price) {

        long orderCode = System.currentTimeMillis() / 1000;

        ItemData itemData = ItemData.builder()
                .name("Order ID : " + orderId)
                .quantity(quantity)
                .price(Math.toIntExact(price))
                .build();

        PaymentData data = PaymentData.builder()
                .orderCode(orderCode)
                .amount(Math.toIntExact(price))

        return payOS.createPaymentLink();
    }

}
