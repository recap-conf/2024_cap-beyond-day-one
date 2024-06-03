const cds = require('@sap/cds'), { UPDATE } = cds.ql

module.exports = srv => {
  srv.after('READ', 'A_BusinessPartnerAddress', async function (addresses) {
    for (const address of addresses) {
      const { AddressID, BusinessPartner } = address
      const { timestamp: readAt, user: { id: readBy } } = cds.context
      await UPDATE('db.A_BusinessPartnerAddress', { AddressID, BusinessPartner }).set({ readAt, readBy })
    }
  })
}
