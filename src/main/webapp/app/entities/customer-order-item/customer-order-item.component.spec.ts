import { beforeEach, describe, expect, it, vitest } from 'vitest';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import CustomerOrderItemService from './customer-order-item.service';
import CustomerOrderItem from './customer-order-item.vue';

type CustomerOrderItemComponentType = InstanceType<typeof CustomerOrderItem>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('CustomerOrderItem Management Component', () => {
    let customerOrderItemServiceStub: SinonStubbedInstance<CustomerOrderItemService>;
    let mountOptions: MountingOptions<CustomerOrderItemComponentType>['global'];

    beforeEach(() => {
      customerOrderItemServiceStub = sinon.createStubInstance<CustomerOrderItemService>(CustomerOrderItemService);
      customerOrderItemServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        toast: {
          show: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          jhiItemCount: true,
          bPagination: true,
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'jhi-sort-indicator': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          customerOrderItemService: () => customerOrderItemServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        customerOrderItemServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(CustomerOrderItem, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(customerOrderItemServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.customerOrderItems[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for an id', async () => {
        // WHEN
        const wrapper = shallowMount(CustomerOrderItem, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(customerOrderItemServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['id,asc'],
        });
      });
    });
    describe('Handles', () => {
      let comp: CustomerOrderItemComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(CustomerOrderItem, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        customerOrderItemServiceStub.retrieve.reset();
        customerOrderItemServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('should load a page', async () => {
        // GIVEN
        customerOrderItemServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.page = 2;
        await comp.$nextTick();

        // THEN
        expect(customerOrderItemServiceStub.retrieve.called).toBeTruthy();
        expect(comp.customerOrderItems[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should not load a page if the page is the same as the previous page', () => {
        // WHEN
        comp.page = 1;

        // THEN
        expect(customerOrderItemServiceStub.retrieve.called).toBeFalsy();
      });

      it('should re-initialize the page', async () => {
        // GIVEN
        comp.page = 2;
        await comp.$nextTick();
        customerOrderItemServiceStub.retrieve.reset();
        customerOrderItemServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.clear();
        await comp.$nextTick();

        // THEN
        expect(comp.page).toEqual(1);
        expect(customerOrderItemServiceStub.retrieve.callCount).toEqual(1);
        expect(comp.customerOrderItems[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for a non-id attribute', async () => {
        // WHEN
        comp.propOrder = 'name';
        await comp.$nextTick();

        // THEN
        expect(customerOrderItemServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['name,asc', 'id'],
        });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        customerOrderItemServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeCustomerOrderItem();
        await comp.$nextTick(); // clear components

        // THEN
        expect(customerOrderItemServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(customerOrderItemServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
