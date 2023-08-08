package com.shepherdmoney.interviewproject.controller;

import com.shepherdmoney.interviewproject.model.BalanceHistory;
import com.shepherdmoney.interviewproject.model.CreditCard;
import com.shepherdmoney.interviewproject.model.User;
import com.shepherdmoney.interviewproject.repository.CreditCardRepository;
import com.shepherdmoney.interviewproject.repository.UserRepository;
import com.shepherdmoney.interviewproject.vo.request.AddCreditCardToUserPayload;
import com.shepherdmoney.interviewproject.vo.request.UpdateBalancePayload;
import com.shepherdmoney.interviewproject.vo.response.CreditCardView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class CreditCardController {

    // TODO: wire in CreditCard repository here (~1 line)
    private final CreditCardRepository creditCardRepository;
    private final UserRepository userRepository;

    public CreditCardController(CreditCardRepository creditCardRepository, UserRepository userRepository){
        this.creditCardRepository = creditCardRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/credit-card")
    public ResponseEntity<Integer> addCreditCardToUser(@RequestBody AddCreditCardToUserPayload payload) {
        // TODO: Create a credit card entity, and then associate that credit card with user with given userId
        //       Return 200 OK with the credit card id if the user exists and credit card is successfully associated with the user
        //       Return other appropriate response code for other exception cases
        //       Do not worry about validating the card number, assume card number could be any arbitrary format and length
        try{
            // Validate the payload.
            if (payload.getCardNumber() == null || payload.getCardIssuanceBank() == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            //Create credit card entity with payload data.
            CreditCard creditCard = new CreditCard();
            creditCard.setIssuanceBank(payload.getCardIssuanceBank());
            creditCard.setNumber(payload.getCardNumber());

            // Associate the credit card with the user with given userId. Throws user not found exception.
            User user = userRepository.findById(payload.getUserId()).orElse(null);
            if (user == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            creditCard.setUser(user);

            //Save new credit card entity
            creditCardRepository.save(creditCard);

            return new ResponseEntity<>(creditCard.getId(), HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/credit-card:all")
    public ResponseEntity<List<CreditCardView>> getAllCardOfUser(@RequestParam int userId) {
        // TODO: return a list of all credit card associated with the given userId, using CreditCardView class
        //       if the user has no credit card, return empty list, never return null
        try {
            List<CreditCard> creditCards = creditCardRepository.findByUserId(userId);

            //Ensures it returns an empty list if the user has no credit cards.
            List<CreditCardView> creditCardViews = new ArrayList<>();

            //Goes through each credit card and creates a new CreditCardView object to return as a part of the list.
            for (CreditCard creditCard : creditCards) {
                CreditCardView creditCardView = new CreditCardView(creditCard.getIssuanceBank(), creditCard.getNumber());
                creditCardViews.add(creditCardView);
            }

            return new ResponseEntity<>(creditCardViews, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/credit-card:user-id")
    public ResponseEntity<Integer> getUserIdForCreditCard(@RequestParam String creditCardNumber) {
        // TODO: Given a credit card number, efficiently find whether there is a user associated with the credit card
        //       If so, return the user id in a 200 OK response. If no such user exists, return 400 Bad Request
        try{
            CreditCard creditCard = creditCardRepository.findByNumber(creditCardNumber);

            if (creditCard != null && creditCard.getUser() != null) {
                return new ResponseEntity<>(creditCard.getUser().getId(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/credit-card:update-balance")
    public ResponseEntity postMethodName(@RequestBody UpdateBalancePayload[] payload) {
        //TODO: Given a list of transactions, update credit cards' balance history.
        //      For example: if today is 4/12, a credit card's balanceHistory is [{date: 4/12, balance: 110}, {date: 4/10, balance: 100}],
        //      Given a transaction of {date: 4/10, amount: 10}, the new balanceHistory is
        //      [{date: 4/12, balance: 120}, {date: 4/11, balance: 110}, {date: 4/10, balance: 110}]
        //      Return 200 OK if update is done and successful, 400 Bad Request if the given card number
        //        is not associated with a card.
        try{
            for(UpdateBalancePayload transaction : payload){
                //Gets information from payload
                String creditCardNumber = transaction.getCreditCardNumber();
                Instant transactionTime = transaction.getTransactionTime();
                double transactionAmount = transaction.getTransactionAmount();

                CreditCard creditCard = creditCardRepository.findByNumber(creditCardNumber);
                if (creditCard == null) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                List<BalanceHistory> balanceHistory = creditCard.getBalanceHistory();

                //Updates every entry at a later date with the new balance
                for (BalanceHistory entry : balanceHistory) {
                    if (!entry.getDate().isBefore(transactionTime)) {
                        double newBalance = entry.getBalance() + transactionAmount;
                        entry.setBalance(newBalance);
                    }
                }

                //If balance history is empty it just creates a new entry.
                if(balanceHistory.isEmpty()){
                    BalanceHistory balanceEntry = new BalanceHistory();
                    balanceEntry.setDate(transactionTime);
                    balanceEntry.setBalance(transactionAmount);
                    balanceHistory.add(0, balanceEntry);
                }

                creditCardRepository.save(creditCard);
            }

            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
