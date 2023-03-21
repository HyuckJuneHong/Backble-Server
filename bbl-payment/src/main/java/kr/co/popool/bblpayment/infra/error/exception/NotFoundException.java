package kr.co.popool.bblpayment.infra.error.exception;

public class NotFoundException extends BusinessLogicException {
    public NotFoundException(String domain) {
        super(String.format("Can not find %s", domain));
    }
}