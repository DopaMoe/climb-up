package com.climbup.event;

import com.climbup.model.Membership;

public record MembershipExpiredEvent(Membership membership) {}