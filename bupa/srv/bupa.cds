using {db} from '../db';

@path: '/sap/opu/odata/sap'
@requires: 'system-user'
service API_BUSINESS_PARTNER {
  entity A_BusinessPartnerAddress as
    projection on db.A_BusinessPartnerAddress {
      key AddressID             as ID,
      key BusinessPartner       as businessPartner,
          @readonly Country     as country,
          @readonly CityName    as city,
          @readonly PostalCode  as postalCode,
          @readonly StreetName  as street,
          @readonly HouseNumber as houseNumber,
          false                 as tombstone : Boolean
    };
}
