package com.obesityPredictAi.api.DTO;

public record PredicaoDto(
    Integer gender,
    Integer age,
    float height,
    float weight,
    Integer familyHistory,
    Integer highCalorieFoods,
    Integer eatVegetables,
    Integer mainMeals,
    Integer foodBetweenMeals,
    Integer smoke,
    float waterIntake,
    Integer monitorCalories,
    Integer physicalActivities,
    float timeTechnologicalDevices,
    Integer alcohol,
    Integer transportation
) {}
