package org.henry.onlinebankingsystemp.controller;

import lombok.RequiredArgsConstructor;
import org.henry.onlinebankingsystemp.dto.AccountRequestDTO;
import org.henry.onlinebankingsystemp.dto.FundAccountDTO;
import org.henry.onlinebankingsystemp.dto.TransactionResponseDTO;
import org.henry.onlinebankingsystemp.dto.VirtualAccountResponse;
import org.henry.onlinebankingsystemp.entity.VirtualAccount;
import org.henry.onlinebankingsystemp.service.VirtualAccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/account")
@RequiredArgsConstructor
public class KoraAPIController {

    private final VirtualAccountService virtualAccount;


    @GetMapping("/get-vba-details")
    public VirtualAccountResponse getVirtualAccountDetails(@RequestBody AccountRequestDTO requestDTO){
        return virtualAccount.getAccountDetails(requestDTO);
    }

    @GetMapping("/get-all-vba-details")
    public List<VirtualAccount> getAllVirtualAccountDetails(){
        return virtualAccount.getAllVirtualAccounts();
    }

    @PostMapping("/create-virtual-account")
    public VirtualAccountResponse createVirtualAccount(@RequestBody AccountRequestDTO request){
        return virtualAccount.createAccount(request);
    }

    @PostMapping("/fundAccount")
    public VirtualAccountResponse creditAccount(@RequestBody FundAccountDTO request){
        return virtualAccount.fundAccount(request);
    }

    @GetMapping("/transactions")
    public TransactionResponseDTO viewTransactions(@RequestBody FundAccountDTO request){
        return virtualAccount.viewTransactions(request);
    }
}
