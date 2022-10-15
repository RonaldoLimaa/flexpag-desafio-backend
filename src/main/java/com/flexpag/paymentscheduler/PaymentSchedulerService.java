package com.flexpag.paymentscheduler;

import com.flexpag.paymentscheduler.PaymentModel;
import com.flexpag.paymentscheduler.PaymentRepository;
import com.flexpag.paymentscheduler.PaymentStatus;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.time.Instant;
import java.util.List;

@Service //determina como uma classe de serviço, que vai fazer as operações com os dados no banco
public class PaymentSchedulerService {
  @Autowired //conecta automaticamente a variável paymentRepository com o repositório(banco de dados)
  private PaymentRepository paymentRepository;

  //método para criação do agendamento para um pagamento no banco, retorna o id do pagamento
  public Long create(PaymentModel payment) {
    payment.setStatus(PaymentStatus.PENDING);            //define status do pagamento como PENDING logo na criação
    PaymentModel savedPayment = paymentRepository.save(payment);      //salva o pagamento no banco de dados e recebe a estrutura salva
    return savedPayment.getId();                         //retorna o id do pagamento salvo no banco
  }

  //método que retorna lista com todos os pagamentos agendados
  public List<PaymentModel> listAllPayments() {
    return paymentRepository.findAll();
  }

  //método que retorna pagamento por id específico
  public PaymentModel findPaymentById(Long id) {
    Optional<PaymentModel> foundPayment = paymentRepository.findById(id); //caso não exista esse pagamento no banco retorna vazio
    return foundPayment.get(); //como fazemos a validação se o valor existe ou não no controller, aqui sempre teremos algo para retornar
  }

  //método que atualiza data e hora do agendamento
  public PaymentModel updateDateAndHourById(Long id, PaymentModel newPayment) {
    PaymentModel foundPayment = findPaymentById(id);
    foundPayment.setDate(newPayment.getDate());  //como validação foi feita no controller, o método apenas altera a data
    return foundPayment;
  }

  //método que deleta agendamento de pagamento
  public void deletePayment(Long id) {
    paymentRepository.deleteById(id); //como validações já foram feitas no controller, o método apenas deleta o agendamento
  }

  //método auxiliar que retorna true se agendamento com id específico existe no banco para validação no controller
  public boolean doesPaymentExistsById(Long id) {
    return paymentRepository.existsById(id);
  }

  //método auxiliar que atualiza status do agendamento
  public void updatePaymentStatus(Long id, PaymentStatus status) {
    PaymentModel foundPayment = findPaymentById(id);
    foundPayment.setStatus(status);
  }
}