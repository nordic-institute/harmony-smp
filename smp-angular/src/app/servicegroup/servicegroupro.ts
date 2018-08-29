export class ServiceGroupRO {
  constructor(public serviceGroupROId: ServiceGroupROId,
              public domain: string

  ) {

  }
}


class ServiceGroupROId {
  constructor(public participantId: string,
              public participantSchema: string
  ) {

  }
}

