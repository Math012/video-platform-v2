import { User } from "./user";

export class Commentary {
  id!: number;
  text!: string;
  uuid_user!: string;
  user!: User;
}
