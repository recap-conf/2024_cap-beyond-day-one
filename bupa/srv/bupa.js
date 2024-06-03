const cds = require('@sap/cds'), { UPDATE } = cds.ql

module.exports = srv => {
  srv.after('READ', 'A_BusinessPartnerAddress', async function (addresses) {
    const { timestamp: readAt, user: { id: readBy } } = cds.context
    for (const address of addresses) {
      const { AddressID, BusinessPartner } = address
      await UPDATE('db.A_BusinessPartnerAddress', { AddressID, BusinessPartner }).set({ readAt, readBy })
    }
  })

  // srv.after('READ', 'A_BusinessPartnerAddress', async function (addresses) {
  //   const { timestamp: readAt, user: { id: readBy } } = cds.context
  //   await UPDATE('db.A_BusinessPartnerAddress')
  //     .set({ readAt, readBy })
  //     .where(`concat(AddressID,':',BusinessPartner) in`, addresses.map(a => `${a.AddressID}:${a.BusinessPartner}`))
  // })
}
