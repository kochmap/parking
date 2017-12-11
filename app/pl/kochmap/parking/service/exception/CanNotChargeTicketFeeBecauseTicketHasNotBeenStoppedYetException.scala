package pl.kochmap.parking.service.exception

import pl.kochmap.parking.domain.DomainException

class CanNotChargeTicketFeeBecauseTicketHasNotBeenStoppedYetException
    extends DomainException(
      "Can't charge ticket fee because ticket hasn't been stopped yet")
