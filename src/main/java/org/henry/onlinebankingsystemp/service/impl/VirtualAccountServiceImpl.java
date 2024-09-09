package org.henry.onlinebankingsystemp.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.henry.onlinebankingsystemp.service.VirtualAccountService;
import org.springframework.stereotype.Service;

@Service @Slf4j
@RequiredArgsConstructor
public class VirtualAccountServiceImpl implements VirtualAccountService {
    //    private final OneTimePasswordUtil accountNumberGenerator;
//    private final AccountRepository accountRepository;
//    private final VirtualAccountRepo virtualAccountRepo;
//    private final UserRepository userRepository;
//
//    private final RestTemplate restTemplate;
//    private static String secretKey = "sk_test_cNNwP1m6g3iDBFDjVDFL7xLFTsgL3WZ2HCsn3diK";
//
//    private String baseUrl = "https://api.korapay.com/merchant/api/v1";
//    private static String VBA = "/virtual-bank-account";
//    private static String VBATransaction = "/virtual-bank-account/transactions";
//
//    private Long getUserId(){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        return ((Customer) authentication.getPrincipal()).getCustomerId();
//    }
//    private Customer getDetails(Long id) {
//        return userRepository.findById(id).orElseThrow(
//                () -> new IllegalStateException("Customer with id " + id + "does not exist"));
//    }
//    private Supplier<Customer> getCurrentUser = () -> { Long id = getUserId(); Customer customer = getDetails(id);
//        return customer;
//    };
//
//    private static HttpHeaders getHttpHeaders(){
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("authorization", "Bearer " + secretKey);
//        return headers;
//    }
//
//    public VirtualAccountResponse createAccount(AccountRequestDTO request) {
//        VirtualAccount account = new VirtualAccount();
//        VirtualAccountResponse res = new VirtualAccountResponse();
//        try {
//            request.setPermanent(true);
//            request.setBank_code("000");
//            request.setAccount_reference(accountNumberGenerator.generateReference());
//            HttpEntity<AccountRequestDTO> httpEntity = new HttpEntity<>(request, getHttpHeaders());
//            String url = baseUrl + VBA;
//            var response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, VirtualAccountResponse.class);
//            AccountCreatedDTO accountInfo = response.getBody().getData();
//
//            account.setAccountName(accountInfo.getAccount_name());
//            account.setAccount_reference(accountInfo.getAccount_reference());
//            account.setAccount_status(accountInfo.getAccount_status());
//            account.setBank_code(accountInfo.getBank_code());
//            account.setBank_name(accountInfo.getBank_name());
//            account.setAccountNumber(account.getAccountNumber());
//            account.setCreated_at(accountInfo.getCreated_at());
//            account.setUnique_id(accountInfo.getUnique_id());
//            account.setCurrency(account.getCurrency());
//            account.setBalance(0.0);
//            account.setCustomerId(getUserId());
//
//            virtualAccountRepo.save(account);
//
//            return response.getBody();
//        }catch (Exception e){
//            res.setStatus(false);
//            res.setMessage(e.getMessage());
//            return res;
//        }
//    }
//
//    public List<VirtualAccount> getAllVirtualAccounts(){
//        Customer customer = getCurrentUser.get();
//        return virtualAccountRepo.findByCustomerId(customer.getCustomerId());
//    }
//
//    public VirtualAccountResponse getAccountDetails(AccountRequestDTO requestDTO){
//        VirtualAccountResponse res = new VirtualAccountResponse();
//        try {
//            HttpEntity<Void> httpEntity = new HttpEntity<>(getHttpHeaders());
//            String url = baseUrl + VBA + "/" + requestDTO.getAccount_reference();
//            System.out.println(url);
//            var response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, VirtualAccountResponse.class);
//            return response.getBody();
//        }catch (Exception e){
//            res.setStatus(false);
//            res.setMessage(e.getMessage());
//            return res;
//        }
//    }
//
//    public VirtualAccountResponse fundAccount(FundAccountDTO fund){
//        VirtualAccountResponse res = new VirtualAccountResponse();
//
//        Customer customer = getCurrentUser.get();
//        Account mainAccount = customer.getAccount();
//
//        if(mainAccount.getBalance().compareTo(BigDecimal.valueOf(fund.getAmount())) < 0){
//            res.setStatus(false);
//            res.setMessage("Insufficient funds in main account to Fund Virtual Account");
//            return res;
//        }
//
//        VirtualAccount virtualAccount = virtualAccountRepo.findByAccountNumber(
//                fund.getAccount_number()).orElseThrow(() -> new UsernameNotFoundException("Account not Found!"));
//        try{
//            fund.setCurrency("NGN");
//            double newBalance = fund.getAmount() + virtualAccount.getBalance();
//            BigDecimal newMainBalance = mainAccount.getBalance().subtract(BigDecimal.valueOf(fund.getAmount()));
//            mainAccount.setBalance(newMainBalance);
//            virtualAccount.setBalance(newBalance);
//            HttpEntity<FundAccountDTO> httpEntity = new HttpEntity<>(fund, getHttpHeaders());
//            String url = baseUrl + VBA + "/sandbox/credit";
//            var response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, VirtualAccountResponse.class);
//            virtualAccountRepo.save(virtualAccount);
//            accountRepository.save(mainAccount);
//
//            return response.getBody();
//        }catch (Exception e){
//            res.setStatus(false);
//            res.setMessage(e.getMessage());
//            return res;
//        }
//    }
//
//    public TransactionResponseDTO viewTransactions(FundAccountDTO fund){
//        TransactionResponseDTO res = new TransactionResponseDTO();
//        try{
//            HttpEntity<FundAccountDTO> httpEntity = new HttpEntity<>(fund, getHttpHeaders());
//            String url = baseUrl + VBATransaction + "?account_number=" + fund.getAccount_number();
//            log.info(url);
//            var response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, TransactionResponseDTO.class);
//            log.info("Response body gotten from Kora");
//            return response.getBody();
//        }catch (Exception e){
//            log.error("Error calling Api");
//            res.setStatus(false);
//            res.setMessage(e.getMessage());
//            return res;
//        }
//    }
}
