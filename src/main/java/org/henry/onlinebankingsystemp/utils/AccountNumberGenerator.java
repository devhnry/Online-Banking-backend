package org.henry.onlinebankingsystemp.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.id.IdentifierGenerationException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountNumberGenerator {
//
//    private final IdGenRepository idGenRepository;
//
//    public String generateAccountNumber(){
//        return generateId();
//    }
//    public String generateReference(){
//        return generateRef();
//    }
//
//
//    private String generateId() {
//        String uniqueValue = String.valueOf(idGenRepository.count());
//        String generatedValue;
//        log.info("Generating Account NUmber");
//        try {
//            String accountNumber = RandomStringUtils.random(11 - uniqueValue.length(), "0123456789");
//            generatedValue = accountNumber + uniqueValue;
//            IdGen refDTO = new IdGen();
//            refDTO.setRef(generatedValue);
//            idGenRepository.save(refDTO);
//
//            return generatedValue;
//        } catch (Exception ex) {
//            log.info("Couldn't generate account Number => {}", ex.getMessage());
//            throw new IdentifierGenerationException("Unexpected error occurred");
//        }
//    }
//
//    private String generateRef(){
//        return getUniqueValue(idGenRepository, log);
//    }
//
//    @NotNull
//    private static String getUniqueValue(IdGenRepository idGenRepository, Logger log) {
//        String uniqueValue = String.valueOf(idGenRepository.count());
//        String generatedValue;
//        log.info("Generating Ref String");
//        try {
//            String ref = RandomStringUtils.random(11 - uniqueValue.length(), "013456789ABCDEFGHIJKLMNOPQRSTUV");
//            generatedValue = ref + uniqueValue;
//            return generatedValue;
//        } catch (Exception ex) {
//            log.info("Couldn't generate transaction reference => {}", ex.getMessage());
//            throw new IdentifierGenerationException("Unexpected error occurred");
//        }
//    }
}


