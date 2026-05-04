package com.climbup.event;

import com.climbup.model.Membership;

public record MembershipCancelledEvent(Membership membership) {}
