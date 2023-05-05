package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        return userRepository.save(user).getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository
        User user = userRepository.findById(userId).get();
        List<WebSeries> webSeriesList = webSeriesRepository.findAll();
        SubscriptionType subscriptionType = user.getSubscription().getSubscriptionType();
        int age = user.getAge();
        int subtype = 0;
        if(subscriptionType == SubscriptionType.BASIC)
            subtype = 1;
        else if(subscriptionType == SubscriptionType.PRO)
            subtype = 2;
        else if(subscriptionType == SubscriptionType.ELITE)
            subtype = 3;

        int count = 0;
        for(WebSeries webSeries : webSeriesList){
            int webtype = 0;
            if(webSeries.getSubscriptionType() == SubscriptionType.BASIC)
                webtype = 1;
            else if(webSeries.getSubscriptionType() == SubscriptionType.PRO)
                webtype = 2;
            else if(webSeries.getSubscriptionType() == SubscriptionType.ELITE)
                webtype = 3;

            if(webSeries.getAgeLimit() < age && subtype >= webtype){
                count++;
            }
        }
        return count;
    }


}
