package com.example.client_leger.profile.viewmodel;

import android.app.Application;
import android.util.Log;

import com.example.client_leger.profile.model.PlayedParty;
import com.example.client_leger.profile.model.User;
import com.example.client_leger.profile.model.UserComputedStats;
import com.example.client_leger.profile.repository.UserRepository;
import com.example.client_leger.utils.LiveResource;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

public class UserViewModel extends AndroidViewModel {
    private UserRepository userRepository;
    private LiveResource<User> user;
    private LiveData<UserComputedStats> userComputedStats;

    private LiveData<String> userFullName;


    public UserViewModel(@NonNull Application application)
    {
        super(application);
        Log.d("ConstructorViewModel", "UserViewModel: ");
        userRepository = UserRepository.getInstance(application);
        user = userRepository.getUser();
        userComputedStats = Transformations.map(user, user -> computeUserStats(user.getData().userStats.matchHistory));
        userFullName = Transformations.map(user, user -> user.getData().name + " " + user.getData().surname);
    }

    public void modifyUser(String nickname, String name, String surname, String password, String avatar) {
        Log.d("viewmodel", "NONE: ");
        userRepository.modifyUser(nickname, name, surname, password, avatar);
    }

    public void viewUser(String nickname) {
        userRepository.viewUser(nickname);
    }

    public LiveResource<User> getUser() {
        return user;
    }

    public LiveData<UserComputedStats> getUserComputedStats() {
        return userComputedStats;
    }

    public LiveData<String> getUserFullName() {
        return userFullName;
    }

    public UserComputedStats computeUserStats(ArrayList<PlayedParty> matchHistory)
    {
        UserComputedStats userComputedStats = new UserComputedStats();

        for (PlayedParty playedParty : matchHistory)
        {
            userComputedStats.totalMatchesPlayed[playedParty.matchType]++;
            userComputedStats.totalWins[playedParty.matchType] += playedParty.won ? 1 : 0;
            userComputedStats.totalTimePlayed[playedParty.matchType] += playedParty.matchDuration;
            if (playedParty.matchScore > userComputedStats.highestScore[playedParty.matchType])
                userComputedStats.highestScore[playedParty.matchType] = playedParty.matchScore;
        }

        for (int i = 1; i < 4; ++i)
        {
            if (userComputedStats.totalMatchesPlayed[i] != 0)
            {
                userComputedStats.averageGameTime[i] = userComputedStats.totalTimePlayed[i] /
                        userComputedStats.totalMatchesPlayed[i];

                userComputedStats.winPercentage[i] = 100 * userComputedStats.totalWins[i] /
                        userComputedStats.totalMatchesPlayed[i];
            }

            userComputedStats.totalMatchesPlayed[0] += userComputedStats.totalMatchesPlayed[i];
            userComputedStats.totalTimePlayed[0] += userComputedStats.totalTimePlayed[i];
            userComputedStats.totalWins[0] += userComputedStats.totalWins[i];
        }

        if (userComputedStats.totalMatchesPlayed[0] != 0)
        {
            userComputedStats.averageGameTime[0] = userComputedStats.totalTimePlayed[0] /
                    userComputedStats.totalMatchesPlayed[0];

            userComputedStats.winPercentage[0] = 100 * userComputedStats.totalWins[0] /
                    userComputedStats.totalMatchesPlayed[0];
        }

        userComputedStats.highestScore[0] = Math.max(Math.max(userComputedStats.highestScore[1],
                userComputedStats.highestScore[2]), userComputedStats.highestScore[3]);

        return userComputedStats;
    }

}
