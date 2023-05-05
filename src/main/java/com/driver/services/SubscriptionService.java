package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        subscription.setStartSubscriptionDate(new Date());

        //For Basic Plan : 500 + 200noOfScreensSubscribed For PRO Plan : 800 + 250noOfScreensSubscribed For ELITE Plan : 1000 + 350*noOfScreensSubscribed
        int totalAmountPaid = 0;
        if(subscription.getSubscriptionType() == SubscriptionType.BASIC){
            totalAmountPaid = 500 + (200 * subscription.getNoOfScreensSubscribed());
        }
        else if(subscription.getSubscriptionType() == SubscriptionType.PRO){
            totalAmountPaid = 800 + (250 * subscription.getNoOfScreensSubscribed());
        }
        else if(subscription.getSubscriptionType() == SubscriptionType.ELITE){
            totalAmountPaid = 1000 + (350 * subscription.getNoOfScreensSubscribed());
        }
        subscription.setTotalAmountPaid(totalAmountPaid);

        user.setSubscription(subscription);
        userRepository.save(user);
        return totalAmountPaid;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user = userRepository.findById(userId).get();
        Subscription subscription = user.getSubscription();
        if(subscription.getSubscriptionType() == SubscriptionType.ELITE){
            throw new Exception("Already the best Subscription");
        }
        int totalUpgradedPayment = 1000+( 350 * subscription.getNoOfScreensSubscribed());
        int prevAmount = subscription.getTotalAmountPaid();
        subscription.setTotalAmountPaid(totalUpgradedPayment);
        subscription.setSubscriptionType(SubscriptionType.ELITE);
        subscriptionRepository.save(subscription);

        return totalUpgradedPayment - prevAmount;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptionList = subscriptionRepository.findAll();
        int total = 0;
        for(Subscription subscription : subscriptionList){
            total += subscription.getTotalAmountPaid();
        }
        return total;
    }

}
