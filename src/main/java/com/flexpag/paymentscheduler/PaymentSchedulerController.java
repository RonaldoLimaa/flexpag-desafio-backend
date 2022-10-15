package com.flexpag.paymentscheduler;

import com.flexpag.paymentscheduler.PaymentSchedulerService;
import com.flexpag.paymentscheduler.PaymentModel;
import com.flexpag.paymentscheduler.PaymentStatus;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

@RestController //determina que essa é uma classe de controle de rotas
@RequestMapping(value = "/payments")  //mapeia rota de acesso para requisições
public class PaymentSchedulerController {
  @Autowired //conecta automaticamente a variável paymentService com a entidade de serviço, que tem contato com o banco de dados
  private PaymentSchedulerService paymentService;

  @GetMapping //mapeia a rota /payments em si e retorna todos os pagamentos agendados no banco
  public ResponseEntity<List<PaymentModel>> findAllPayments() {
    List<PaymentModel> payments = paymentService.listAllPayments();
    return ResponseEntity.status(HttpStatus.OK).body(payments);
  }

  @PostMapping(value = "/create") //mapeia rota para criação do agendamento
  public ResponseEntity<Object> createPayment(@RequestBody PaymentModel payment) {
    if (payment.getDate().isBefore(Instant.now())) { //valida se a hora de pagamento é após a hora de cadastro
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("O pagamento não pode ser agendado para datas passadas!");
    } else {
      Long paymentId = paymentService.create(payment);  //pagamento é salvo no banco de dados
      return ResponseEntity.status(HttpStatus.OK).body(paymentId);  //retorna o id do agendamento
    }
  }

  @GetMapping(value = "/{id}") //mapeia rota para retornar agendamento pelo id 
  public ResponseEntity<Object> findPaymentById(@PathVariable Long id) {
    boolean doesPaymentExists = paymentService.doesPaymentExistsById(id);
    if(doesPaymentExists) { //valida se o pagamento existe no banco de dados
      PaymentModel payment = paymentService.findPaymentById(id);
      if (payment.getDate().equals(Instant.now()) || payment.getDate().isBefore(Instant.now())) { //valida se a data e hora do agendamento já passou
        paymentService.updatePaymentStatus(id, PaymentStatus.PAID); //atualiza status do pagamento
      }
      return ResponseEntity.status(HttpStatus.OK).body(payment);  //retorna o pagamento existente para id
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não existe pagamento agendado com esse id!");  //se pagamento não existe, retorna que não foi encontrado
    }  
  }

  @GetMapping(value = "status/{id}") //mapeia rota para retornar o status do agendamento com id específico 
  public ResponseEntity<Object> findPaymentStatusById(@PathVariable Long id) {
    boolean doesPaymentExists = paymentService.doesPaymentExistsById(id);
    if(doesPaymentExists) { //valida se o pagamento existe no banco de dados
      PaymentModel payment = paymentService.findPaymentById(id);
      if (payment.getDate().equals(Instant.now()) || payment.getDate().isBefore(Instant.now())) { //valida se a data e hora do agendamento já passou
        paymentService.updatePaymentStatus(id, PaymentStatus.PAID); //atualiza status do pagamento
      }
      return ResponseEntity.status(HttpStatus.OK).body(payment.getStatus());  //retorna o status do agendamento existente para id
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não existe pagamento agendado com esse id!");  //se pagamento não existe, retorna que não foi encontrado
    }  
  }

  @PutMapping(value = "/update/{id}") //mapeia rota para atualizar data e hora do agendamento
  public ResponseEntity<Object> updateDateAndHour(@PathVariable Long id, @RequestBody PaymentModel newPayment) {
    boolean doesPaymentExists = paymentService.doesPaymentExistsById(id);
    if(doesPaymentExists) { //valida se o pagamento existe no banco de dados
      PaymentModel payment = paymentService.findPaymentById(id);
      if (payment.getDate().equals(Instant.now()) || payment.getDate().isBefore(Instant.now())) { //valida se a data e hora do agendamento já passou
        paymentService.updatePaymentStatus(id, PaymentStatus.PAID); //atualiza status do pagamento
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("O pagamento já foi realizado!"); //não pode alterar após pagamento ter sido realizado
      } else {
        if (newPayment.getDate().isBefore(Instant.now())) { //valida se a nova hora de pagamento é após a hora de cadastro
          return ResponseEntity.status(HttpStatus.FORBIDDEN).body("O pagamento não pode ser agendado para datas passadas!");
        } else {
          PaymentModel updatedPayment = paymentService.updateDateAndHourById(id, newPayment); //atualiza data e hora do pagamento
          return ResponseEntity.status(HttpStatus.OK).body(updatedPayment);  //retorna o pagamento existente para id
        }
      }
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não existe pagamento agendado com esse id!");  //se pagamento não existe, retorna que não foi encontrado
    }  
  }

  @DeleteMapping(value = "/delete/{id}")
  public ResponseEntity<Object> delete(@PathVariable Long id) {
    boolean doesPaymentExists = paymentService.doesPaymentExistsById(id);
    if(doesPaymentExists) { //valida se o pagamento existe no banco de dados
      PaymentModel payment = paymentService.findPaymentById(id);
      if (payment.getDate().equals(Instant.now()) || payment.getDate().isBefore(Instant.now())) { //valida se a data e hora do agendamento já passou
        paymentService.updatePaymentStatus(id, PaymentStatus.PAID); //atualiza status do pagamento
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("O pagamento já foi realizado!"); //não pode alterar após pagamento ter sido realizado
      } else {
          paymentService.deletePayment(id);  //deleta agendamento do banco de dados
          return ResponseEntity.status(HttpStatus.OK).body("O agendamento foi cancelado!");  
      }
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não existe pagamento agendado com esse id!");  //se pagamento não existe, retorna que não foi encontrado
    }  
  }

}