package com.i0dev.events.entity.object;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PlacedReward {

    int place;
    List<String> commands;

}
