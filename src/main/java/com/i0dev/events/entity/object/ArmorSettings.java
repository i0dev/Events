package com.i0dev.events.entity.object;

import com.massivecraft.massivecore.item.DataItemStack;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter@Builder@Data
public class ArmorSettings {

    DataItemStack helmet;
    DataItemStack chestplate;
    DataItemStack leggings;
    DataItemStack boots;

}
