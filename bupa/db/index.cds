using {User} from '@sap/cds/common';

context db {
  entity A_BusinessPartnerAddress {
    key BusinessPartner : String(10);
    key AddressID       : String(10);
        Country         : String(3);
        CityName        : String(40);
        PostalCode      : String(10);
        StreetName      : String(60);
        HouseNumber     : String(10);
        readAt          : Timestamp;
        readBy          : User;
  }
}
