using {db} from '../db';

@path: '/sap/opu/odata/sap'
@requires: 'system-user'
service API_BUSINESS_PARTNER {
  entity A_BusinessPartnerAddress as
    projection on db.A_BusinessPartnerAddress {
      key AddressID,
      key BusinessPartner,
          @readonly Country,
          @readonly CityName,
          @readonly PostalCode,
          @readonly StreetName,
          @readonly HouseNumber,
          false                 as tombstone : Boolean
    };
}
