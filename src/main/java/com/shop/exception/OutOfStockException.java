package com.shop.exception;

// 재고 부족 예외 클래스
public class OutOfStockException extends RuntimeException{
    // OutOfStockException 생성자
    public OutOfStockException(String message) {
        super(message);
    }
}
