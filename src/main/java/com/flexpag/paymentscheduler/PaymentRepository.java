package com.flexpag.paymentscheduler;

import com.flexpag.paymentscheduler.PaymentModel;

import org.springframework.data.jpa.repository.JpaRepository; //framework de spring boot que gerencia o banco de dados 

public interface PaymentRepository extends JpaRepository<PaymentModel, Long>{
}
