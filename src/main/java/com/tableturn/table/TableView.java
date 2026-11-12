package com.tableturn.table;

import com.fasterxml.jackson.annotation.JsonInclude;

/** One table on the floor; a seated table also shows its party and how many minutes it has been sitting. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TableView(String no,int seats,Section section,TableState state,String partyId,
Integer seatedMinutes){
}
